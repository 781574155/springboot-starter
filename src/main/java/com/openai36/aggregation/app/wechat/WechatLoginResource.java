package com.openai36.aggregation.app.wechat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai36.aggregation.common.Constants;
import com.openai36.aggregation.eao.*;
import com.openai36.aggregation.service.UserService;
import com.openai36.aggregation.service.WechatLoginCache;
import com.openai36.aggregation.service.dto.WechatLogin;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("wechat")
public class WechatLoginResource {
    @Inject
    private SystemConfigRepository configRepository;
    @Inject
    private WechatUserRepository wechatUserRepository;
    @Inject
    private UserService userService;
    @Inject
    private RestClient restClient;
    @Inject
    private WechatLoginCache wechatLoginCache;
    @Inject
    private UserRepository userRepository;
    @Inject
    private ObjectMapper objectMapper;

    /**
     * 这是微信扫码登录，手机从微信服务器重定向回来的接口
     * 登录流程如下：首先微信会因snsapi_base重定向到此接口。如果接口检测到用户不存在，会以snsapi_userinfo重定向到微信，微信再重定向回来
     * 从而做到首次登录，获取用户信息，后续登录无感
     */
    @SneakyThrows
    @Transactional
    @GetMapping("login")
    public ModelAndView login(@RequestParam String code, @RequestParam String state) {
        log.debug("微信登录：code={}, state={}", code, state);
        SystemConfigEntity config = configRepository.findById(Constants.DEFAULT).get();
        if (!config.getEnableWechatLogin()) {
            return createFailure("微信登录未启用");
        }

        if (StringUtils.isEmpty(code)) {
            return createFailure("code不能为空");
        }

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + config.getWechatAppId() +
                "&secret=" + config.getWechatAppSecret() +
                "&code=" + code +
                "&grant_type=authorization_code";
        String s = restClient.get().uri(url).retrieve().body(String.class);
        Map m = objectMapper.readValue(s, Map.class);
        if (m.containsKey("errcode") && (Integer)m.get("errcode") != 0) {
            return createFailure("微信登录失败：" + m.get("errmsg"));
        }

        String openid = (String) m.get("openid");
        if (StringUtils.isEmpty(openid)) {
            return createFailure("微信登录失败：openid为空");
        }

        Optional<WechatUserEntity> wueOp = wechatUserRepository.findByOpenId(openid);
        if (wueOp.isPresent()) {
            WechatLogin wechatLogin = wechatLoginCache.get(state);
            wechatLogin.setUserId(wueOp.get().getUserId());
            wechatLogin.setTmpPassword(RandomStringUtils.randomAlphanumeric(12));
            return createSuccess();
        }

        if (!config.getEnableWechatRegister()) {
            return createFailure("微信登录失败：用户不存在");
        }

        String scope = (String) m.get("scope");
        if (!scope.contains("snsapi_userinfo")) {
            String rdiUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + config.getWechatAppId() +
                    "&redirect_uri=" + URLEncoder.encode("https://www.openai36.com/api/wechat/login", StandardCharsets.UTF_8) +
                    "&response_type=code&scope=snsapi_userinfo&state=" + state +
                    "#wechat_redirect";
            return new ModelAndView("redirect:"+ rdiUrl);
        }

        String accessToken = (String) m.get("access_token");
        String url2 = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken +
                "&openid=" + openid +
                "&lang=zh_CN";
        byte[] b2 = restClient.get().uri(url2).retrieve().body(byte[].class);
        String s2 = new String(b2, StandardCharsets.UTF_8);
        Map m2 = objectMapper.readValue(s2, Map.class);
        if (m2.containsKey("errcode")) {
            return createFailure("微信登录失败2：" + m.get("errmsg"));
        }

        String username = "wx"+RandomStringUtils.randomNumeric(6);
        while (userRepository.findByUsername(username).isPresent()) {
            username = "wx"+RandomStringUtils.randomNumeric(6);
        }
        UserEntity userEntity = userService.createUser(username, "gen_"+RandomStringUtils.randomAlphanumeric(12));
        WechatUserEntity wue = new WechatUserEntity();
        wue.setUserId(userEntity.getId());
        wue.setOpenId(openid);
        wue.setNickname(getStringValue(m2, "nickname"));
        wue.setSex(getStringValue(m2, "sex"));
        wue.setProvince(getStringValue(m2, "province"));
        wue.setCity(getStringValue(m2, "city"));
        wue.setCountry(getStringValue(m2, "country"));
        wue.setHeadimgurl(getStringValue(m2, "headimgurl"));
        wue.setPrivilege(getStringValue(m2, "privilege"));
        wue.setUnionid(getStringValue(m2, "unionid"));
        wue.setCreateTime(LocalDateTime.now());
        wechatUserRepository.save(wue);

        WechatLogin wechatLogin = wechatLoginCache.get(state);
        wechatLogin.setUserId(userEntity.getId());
        wechatLogin.setTmpPassword(RandomStringUtils.randomAlphanumeric(12));
        return createSuccess();
    }

    /**
     * 这是手机用微信浏览器访问登录页面，登录页面重定向到微信服务器，手机从微信服务器重定向回来的接口
     * 登录流程如下：首先微信会因snsapi_base重定向到此接口。如果接口检测到用户不存在，会以snsapi_userinfo重定向到微信，微信再重定向回来
     * 从而做到首次登录，获取用户信息，后续登录无感
     */
    @SneakyThrows
    @Transactional
    @GetMapping("login2")
    public ModelAndView login2(@RequestParam String code) {
        log.debug("微信登录：code={}", code);
        SystemConfigEntity config = configRepository.findById(Constants.DEFAULT).get();
        if (!config.getEnableWechatLogin()) {
            return createFailure2("微信登录未启用");
        }

        if (StringUtils.isEmpty(code)) {
            return createFailure2("code不能为空");
        }

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + config.getWechatAppId() +
                "&secret=" + config.getWechatAppSecret() +
                "&code=" + code +
                "&grant_type=authorization_code";
        String s = restClient.get().uri(url).retrieve().body(String.class);
        Map m = objectMapper.readValue(s, Map.class);
        if (m.containsKey("errcode") && (Integer)m.get("errcode") != 0) {
            return createFailure2("微信登录失败：" + m.get("errmsg"));
        }

        String openid = (String) m.get("openid");
        if (StringUtils.isEmpty(openid)) {
            return createFailure2("微信登录失败：openid为空");
        }

        Optional<WechatUserEntity> wueOp = wechatUserRepository.findByOpenId(openid);
        if (wueOp.isPresent()) {
            return createSuccess2(wueOp.get().getUserId());
        }

        if (!config.getEnableWechatRegister()) {
            return createFailure2("微信登录失败：用户不存在");
        }

        String scope = (String) m.get("scope");
        if (!scope.contains("snsapi_userinfo")) {
            String rdiUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + config.getWechatAppId() +
                    "&redirect_uri=" + URLEncoder.encode("https://www.openai36.com/api/wechat/login2", StandardCharsets.UTF_8) +
                    "&response_type=code&scope=snsapi_userinfo#wechat_redirect";
            return new ModelAndView("redirect:"+ rdiUrl);
        }

        String accessToken = (String) m.get("access_token");
        String url2 = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken +
                "&openid=" + openid +
                "&lang=zh_CN";
        byte[] b2 = restClient.get().uri(url2).retrieve().body(byte[].class);
        String s2 = new String(b2, StandardCharsets.UTF_8);
        Map m2 = objectMapper.readValue(s2, Map.class);
        if (m2.containsKey("errcode")) {
            return createFailure2("微信登录失败2：" + m.get("errmsg"));
        }

        String username = "wx"+RandomStringUtils.randomNumeric(6);
        while (userRepository.findByUsername(username).isPresent()) {
            username = "wx"+RandomStringUtils.randomNumeric(6);
        }
        UserEntity userEntity = userService.createUser(username, "gen_"+RandomStringUtils.randomAlphanumeric(12));
        WechatUserEntity wue = new WechatUserEntity();
        wue.setUserId(userEntity.getId());
        wue.setOpenId(openid);
        wue.setNickname(getStringValue(m2, "nickname"));
        wue.setSex(getStringValue(m2, "sex"));
        wue.setProvince(getStringValue(m2, "province"));
        wue.setCity(getStringValue(m2, "city"));
        wue.setCountry(getStringValue(m2, "country"));
        wue.setHeadimgurl(getStringValue(m2, "headimgurl"));
        wue.setPrivilege(getStringValue(m2, "privilege"));
        wue.setUnionid(getStringValue(m2, "unionid"));
        wue.setCreateTime(LocalDateTime.now());
        wechatUserRepository.save(wue);

        return createSuccess2(userEntity.getId());
    }

    //////////////////////////////////////////////////////////////////////////////
    private String getStringValue(Map m, String key) {
        if (!m.containsKey(key)) {
            return null;
        }
        Object v = m.get(key);
        if (v instanceof String) {
            return (String) v;
        }
        return v.toString();
    }

    private ModelAndView createFailure(String message) {
        ModelAndView mav = new ModelAndView("wechat_login_result");
        mav.addObject("message", message);
        mav.addObject("success", false);
        return mav;
    }

    private ModelAndView createSuccess() {
        ModelAndView mav = new ModelAndView("wechat_login_result");
        mav.addObject("message", "登录成功");
        mav.addObject("success", true);
        return mav;
    }

    private ModelAndView createFailure2(String message) {
        log.info(message);
        ModelAndView mav = new ModelAndView("login");
        return mav;
    }

    private ModelAndView createSuccess2(Integer userId) {
        WechatLogin wl = new WechatLogin();
        wl.setSuccess(true);
        wl.setUserId(userId);
        wl.setTmpPassword(RandomStringUtils.randomAlphanumeric(12));
        String state = RandomStringUtils.randomAlphanumeric(16);
        wechatLoginCache.put(state, wl);

        SystemConfigEntity config = configRepository.findById(Constants.DEFAULT).get();
        ModelAndView mav = new ModelAndView("wechat_login");
        mav.addObject("enablePageLogin", config.getEnablePageLogin());
        mav.addObject("enablePageRegister", config.getEnablePageRegister());
        mav.addObject("enableWechatLogin", config.getEnableWechatLogin());
        mav.addObject("username", state);
        mav.addObject("password", wl.getTmpPassword());
        return mav;
    }
}

package com.openai36.aggregation.app.pub;

import com.openai36.aggregation.app.rep.UserDto;
import com.openai36.aggregation.common.Constants;
import com.openai36.aggregation.eao.SystemConfigEntity;
import com.openai36.aggregation.eao.SystemConfigRepository;
import com.openai36.aggregation.eao.UserEntity;
import com.openai36.aggregation.eao.UserRepository;
import com.openai36.aggregation.service.UserService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Controller
public class RegisterResource {
    @Inject
    private UserRepository repository;
    @Inject
    private UserService userService;
    @Inject
    private SystemConfigRepository configRepository;

    @SneakyThrows
    @GetMapping("login")
    public String login(Model model, @RequestParam(name = "form", required = false) Boolean form, @RequestHeader("User-Agent") String userAgent) {
        SystemConfigEntity config = configRepository.findById(Constants.DEFAULT).get();
        model.addAttribute("enablePageLogin", config.getEnablePageLogin());
        model.addAttribute("enablePageRegister", config.getEnablePageRegister());
        model.addAttribute("enableWechatLogin", config.getEnableWechatLogin());
        if (form != null) {
            model.addAttribute("enablePageLogin", form);
        }

        // 从微信浏览器访问
        if (Constants.WECHAT_PATTERN.matcher(userAgent).find() && config.getEnableWechatLogin()) {
            String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + config.getWechatAppId() +
                    "&redirect_uri=" + URLEncoder.encode("https://www.openai36.com/api/wechat/login2", StandardCharsets.UTF_8) +
                    "&response_type=code&scope=snsapi_base#wechat_redirect";
            return "redirect:"+url;
        }
        return "login";
    }

    @GetMapping("register")
    public String register(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("register")
    @Transactional
    public String register(Model model, @ModelAttribute("user") @Valid UserDto user) {
        if (!isAlphanumeric(user.getUsername())) {
            model.addAttribute("error", true);
            model.addAttribute("message", "注册失败：用户名只能由字母和数字组成");
            return "register";
        }
        if (user.getUsername().length() < 5 || user.getUsername().length() > 20) {
            model.addAttribute("error", true);
            model.addAttribute("message", "注册失败：用户名长度为5到20位");
            return "register";
        }
        if (user.getPassword().length() < 5 || user.getPassword().length() > 32) {
            model.addAttribute("error", true);
            model.addAttribute("message", "注册失败：密码最小长度为5位，最大为32位");
            return "register";
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("error", true);
            model.addAttribute("message", "注册失败：两次密码输入不一致");
            return "register";
        }

        if (!configRepository.findById(Constants.DEFAULT).get().getEnablePageRegister()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "系统未启用页面注册功能");
            return "register";
        }

        Optional<UserEntity> ueOp = repository.findByUsername(user.getUsername());
        model.addAttribute("time", LocalDateTime.now());
        model.addAttribute("user", user);
        if (ueOp.isPresent()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "注册失败：该用户名已被注册");
            return "register";
        }

        userService.createUser(user.getUsername(), user.getPassword());
        model.addAttribute("username", user.getUsername());
        return "register_success";
    }

    /////////////////////////////////////////////////////////////
    private static boolean isAlphanumeric(String str) {
        if(str == null || str.isEmpty()){
            return false;
        }
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }
}

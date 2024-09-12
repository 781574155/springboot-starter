package com.openai36.aggregation.app.wechat;

import com.openai36.aggregation.app.wechat.rep.WechatLoginCreateState;
import com.openai36.aggregation.app.wechat.rep.WechatValidCreateState;
import com.openai36.aggregation.common.Constants;
import com.openai36.aggregation.common.GeneralOperationResult;
import com.openai36.aggregation.eao.SystemConfigEntity;
import com.openai36.aggregation.eao.SystemConfigRepository;
import com.openai36.aggregation.service.WechatLoginCache;
import com.openai36.aggregation.service.dto.WechatLogin;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("wechat")
public class WechatLoginRequestResource {
    @Inject
    private SystemConfigRepository configRepository;
    @Inject
    private WechatLoginCache wechatLoginCache;

    @PostMapping("create_login_request")
    public GeneralOperationResult<WechatLoginCreateState> createLoginRequest() {
        SystemConfigEntity config = configRepository.findById(Constants.DEFAULT).get();
        if (!config.getEnableWechatLogin()) {
            return GeneralOperationResult.failure("微信登录未启用");
        }

        WechatLogin wl = new WechatLogin();
        wl.setSuccess(false);
        String state = RandomStringUtils.randomAlphanumeric(16);
        wechatLoginCache.put(state, wl);
        WechatLoginCreateState ws = new WechatLoginCreateState(state, config.getWechatAppId());
        return GeneralOperationResult.success(ws);
    }

    @GetMapping("valid_login_request")
    public GeneralOperationResult<WechatValidCreateState> validLoginRequest(@RequestParam String state) {
        SystemConfigEntity config = configRepository.findById(Constants.DEFAULT).get();
        if (!config.getEnableWechatLogin()) {
            return GeneralOperationResult.failure("微信登录未启用");
        }

        WechatLogin wl = wechatLoginCache.get(state);
        if (wl == null) {
            WechatValidCreateState wvcs = new WechatValidCreateState();
            wvcs.setValidKey(false);
            return GeneralOperationResult.success(wvcs);
        }

        WechatValidCreateState wvcs = new WechatValidCreateState();
        wvcs.setValidKey(true);
        if (wl.getUserId() == null) {
            wvcs.setLogin(false);
            return GeneralOperationResult.success(wvcs);
        }

        wvcs.setLogin(true);
        wvcs.setTmpPassword(wl.getTmpPassword());
        return GeneralOperationResult.success(wvcs);
    }
}

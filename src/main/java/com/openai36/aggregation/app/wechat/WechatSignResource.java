package com.openai36.aggregation.app.wechat;

import com.openai36.aggregation.app.wechat.rep.WechatSignRep;
import com.openai36.aggregation.common.Constants;
import com.openai36.aggregation.common.GeneralOperationResult;
import com.openai36.aggregation.common.StringResult;
import com.openai36.aggregation.eao.SystemConfigEntity;
import com.openai36.aggregation.eao.SystemConfigRepository;
import com.openai36.aggregation.service.WechatService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

import static com.google.common.hash.Hashing.sha1;

@Slf4j
@RestController
@RequestMapping("wechat")
public class WechatSignResource {
    @Inject
    private SystemConfigRepository configRepository;
    @Inject
    private WechatService wechatService;


    @GetMapping("sign/share")
    public GeneralOperationResult<WechatSignRep> signShare(@RequestParam("uuid") @NotBlank String uuid) {
        GeneralOperationResult<StringResult> jsapiTicket = wechatService.getJsapiTicket();
        if (!jsapiTicket.isSuccess()) {
            return GeneralOperationResult.failure(jsapiTicket.getMessage());
        }
        String ticket = jsapiTicket.getData().getMessage();

        String nonceStr = RandomStringUtils.randomAlphanumeric(16);
        Long timestamp = System.currentTimeMillis() / 1000;
        String url = "https://www.openai36.com/share/" + uuid;

        String signature = sign(ticket, nonceStr, timestamp, url);

        WechatSignRep rep = new WechatSignRep();
        SystemConfigEntity config = configRepository.findById(Constants.DEFAULT).get();
        rep.setAppId(config.getWechatAppId());
        rep.setTimestamp(timestamp);
        rep.setNonceStr(nonceStr);
        rep.setSignature(signature);

        return GeneralOperationResult.success(rep);
    }


    private String sign(String ticket, String nonceStr, Long timestamp, String url) {
        String str = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
        return sha1().hashString(str, StandardCharsets.UTF_8).toString();
    }
}

package com.openai36.aggregation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.openai36.aggregation.common.Constants;
import com.openai36.aggregation.common.GeneralOperationResult;
import com.openai36.aggregation.common.StringResult;
import com.openai36.aggregation.eao.SystemConfigEntity;
import com.openai36.aggregation.eao.SystemConfigRepository;
import com.openai36.aggregation.service.dto.WechatLogin;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Singleton
public class WechatService {
    @Inject
    private SystemConfigRepository configRepository;
    @Inject
    private RestClient restClient;
    @Inject
    private ObjectMapper objectMapper;

    private static final String AccsessToken = "access_token";
    private static final String JsapiTicket = "jsapi_ticket";

    private final Cache<String, String> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .maximumSize(10)
            .build();

    @SneakyThrows
    public GeneralOperationResult<StringResult> getJsapiTicket() {
        String jsapiTicket = cache.getIfPresent(JsapiTicket);
        if (jsapiTicket != null) {
            return GeneralOperationResult.success(jsapiTicket);
        }

        GeneralOperationResult<StringResult> accessToken = getAccessToken();
        if (!accessToken.isSuccess()) {
            return accessToken;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken.getData().getMessage() +
                "&type=jsapi";
        String s = restClient.get().uri(url).retrieve().body(String.class);
        Map m = objectMapper.readValue(s, Map.class);
        if (m.containsKey("errcode") && (Integer)m.get("errcode") != 0) {
            return GeneralOperationResult.failure("获取微信JsapiTicket失败：" + m.get("errmsg"));
        }
        jsapiTicket = (String)m.get("ticket");
        cache.put(JsapiTicket, jsapiTicket);
        return GeneralOperationResult.success(new StringResult(jsapiTicket));
    }

    @SneakyThrows
    public GeneralOperationResult<StringResult> getAccessToken() {
        String accessToken = cache.getIfPresent(AccsessToken);
        if (accessToken != null) {
            return GeneralOperationResult.success(accessToken);
        }
        SystemConfigEntity config = configRepository.findById(Constants.DEFAULT).get();
        if (!config.getEnableWechatShare()) {
            return GeneralOperationResult.failure("微信分享未启用");
        }

        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + config.getWechatAppId() +
                "&secret=" + config.getWechatAppSecret();
        String s = restClient.get().uri(url).retrieve().body(String.class);
        Map m = objectMapper.readValue(s, Map.class);
        if (m.containsKey("errcode") && (Integer)m.get("errcode") != 0) {
            return GeneralOperationResult.failure("获取微信AccessToken失败：" + m.get("errmsg"));
        }
        accessToken = (String)m.get("access_token");
        cache.put(AccsessToken, accessToken);
        return GeneralOperationResult.success(new StringResult(accessToken));
    }
}

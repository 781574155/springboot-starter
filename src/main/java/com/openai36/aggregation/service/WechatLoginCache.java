package com.openai36.aggregation.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.openai36.aggregation.service.dto.WechatLogin;
import jakarta.inject.Singleton;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Singleton
public class WechatLoginCache {
    private final Cache<String, WechatLogin> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    public void put(String key, WechatLogin value) {
        cache.put(key, value);
    }

    public WechatLogin get(String key) {
        return cache.getIfPresent(key);
    }

    public void remove(String key) {
        cache.invalidate(key);
    }
}

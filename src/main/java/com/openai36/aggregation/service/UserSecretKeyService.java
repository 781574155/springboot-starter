package com.openai36.aggregation.service;

import com.openai36.aggregation.common.Constants;
import com.openai36.aggregation.common.DateTimeUtil;
import com.openai36.aggregation.eao.UserSecretKeyEntity;
import com.openai36.aggregation.eao.UserSecretKeyRepository;
import com.openai36.aggregation.service.event.UserCreatedEvent;
import com.openai36.aggregation.service.event.UserDeleteEvent;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserSecretKeyService {
    @Inject
    private UserSecretKeyRepository repository;

    public UserSecretKeyEntity createUserSecretKey(Integer userId, String name) {
        return createUserSecretKey(userId, name, false);
    }

    @EventListener
    void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("为用户{}创建默认Secret", event.getUsername());
        createUserSecretKey(event.getUserId(), Constants.DEFAULT, true);
    }

    @EventListener
    void handleUserDeleteEvent(UserDeleteEvent event) {
        log.info("删除用户{}的Secret", event.getUserId());

        repository.deleteByUserId(event.getUserId());
    }

    /////////////////////////////////////////////////////////////////////
    private UserSecretKeyEntity createUserSecretKey(Integer userId, String name, boolean defaultKey) {
        UserSecretKeyEntity entity = new UserSecretKeyEntity();
        if (StringUtils.isBlank(name)) {
            entity.setName("Key-"+ DateTimeUtil.currentDateTime());
        } else {
            entity.setName(name);
        }
        entity.setSecretKey("sk-"+ RandomStringUtils.randomAlphanumeric(48));
        entity.setUserId(userId);
        entity.setCreateTime(LocalDateTime.now());
        entity.setDefaultKey(defaultKey);

        repository.save(entity);
        return entity;
    }
}

package com.openai36.aggregation.service;

import com.openai36.aggregation.common.Constants;
import com.openai36.aggregation.eao.SystemConfigEntity;
import com.openai36.aggregation.eao.SystemConfigRepository;
import com.openai36.aggregation.eao.UserEntity;
import com.openai36.aggregation.eao.UserRepository;
import com.openai36.aggregation.security.Roles;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class InitService {
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserService userService;
    @Inject
    private SystemConfigRepository systemConfigRepository;

    @EventListener
    void createDefault(ContextRefreshedEvent event) {
        Page<UserEntity> pu = userRepository.findAll(PageRequest.of(0, 1));
        if (pu.getTotalElements() == 0) {
            log.info("系统中没有用户，创建默认用户");
            userService.createUser(Constants.ADMIN, RandomStringUtils.randomAlphanumeric(8), List.of(Roles.ROLE_ADMIN, Roles.ROLE_USER));
        }

        systemConfigRepository.findById(Constants.DEFAULT).ifPresentOrElse(
                sc -> {},
                () -> {
                    log.info("系统配置不存在，创建默认配置");
                    SystemConfigEntity entity = new SystemConfigEntity();
                    entity.setName(Constants.DEFAULT);
                    entity.setEnablePageRegister(false);
                    entity.setEnablePageLogin(true);
                    entity.setEnableWechatRegister(false);
                    entity.setEnableWechatLogin(false);
                    entity.setEnableWechatShare(false);
                    systemConfigRepository.save(entity);
                }
        );
    }
}

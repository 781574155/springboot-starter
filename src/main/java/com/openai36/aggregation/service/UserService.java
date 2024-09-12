package com.openai36.aggregation.service;

import com.openai36.aggregation.common.GeneralOperationResult;
import com.openai36.aggregation.eao.*;
import com.openai36.aggregation.security.Roles;
import com.openai36.aggregation.service.event.UserCreatedEvent;
import com.openai36.aggregation.service.event.UserDeleteEvent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserService {
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserRoleRepository userRoleRepository;
    @Inject
    private WechatUserRepository wechatUserRepository;
    @Inject
    private ApplicationEventPublisher eventPublisher;

    public UserEntity createUser(String username, String password) {
        return createUser(username, password, List.of(Roles.ROLE_USER));
    }

    @Transactional
    public UserEntity createUser(String username, String password, List<String> roles) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(password);
        userEntity.setEnabled(true);
        userEntity.setCreateTime(LocalDateTime.now());
        userRepository.save(userEntity);

        roles.forEach(role -> {
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setUserId(userEntity.getId());
            userRoleEntity.setRole(role);
            userRoleEntity.setCreateTime(LocalDateTime.now());
            userRoleRepository.save(userRoleEntity);
        });

        eventPublisher.publishEvent(new UserCreatedEvent(userEntity.getId(), userEntity.getUsername()));

        return userEntity;
    }

    @Transactional
    public GeneralOperationResult<?> deleteUser(Integer userId) {
        log.info("delete user, userId = {}", userId);

        List<UserRoleEntity> roles = userRoleRepository.findByUserId(userId);
        List<String> rs = roles.stream().map(UserRoleEntity::getRole).toList();
        if (rs.contains(Roles.ROLE_ADMIN)) {
            log.warn("admin user can not be deleted");
            return GeneralOperationResult.failure("admin user can not be deleted");
        }

        userRoleRepository.deleteByUserId(userId);

        wechatUserRepository.deleteByUserId(userId);

        eventPublisher.publishEvent(new UserDeleteEvent(userId));

        userRepository.deleteById(userId);

        return GeneralOperationResult.success();
    }
}

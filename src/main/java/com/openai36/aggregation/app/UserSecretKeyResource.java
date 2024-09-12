package com.openai36.aggregation.app;

import com.openai36.aggregation.common.GeneralOperationResult;
import com.openai36.aggregation.eao.UserSecretKeyEntity;
import com.openai36.aggregation.eao.UserSecretKeyRepository;
import com.openai36.aggregation.security.Login;
import com.openai36.aggregation.security.SecurityUtil;
import com.openai36.aggregation.service.UserSecretKeyService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Login
@RestController
@RequestMapping("/user/secrets")
public class UserSecretKeyResource {
    @Inject
    private UserSecretKeyRepository repository;
    @Inject
    private UserSecretKeyService userSecretKeyService;
    @Inject
    private SecurityUtil securityUtil;

    @Transactional
    @PostMapping
    public GeneralOperationResult<UserSecretKeyEntity> create(Authentication authentication, String name) {
        List<UserSecretKeyEntity> ks = repository.findByUserId(securityUtil.getUserId(authentication)).stream().filter(k -> !k.getDefaultKey()).toList();
        if (ks.size() >= 5) {
            return GeneralOperationResult.failure("最多只能创建5个密钥");
        }
        UserSecretKeyEntity entity = userSecretKeyService.createUserSecretKey(securityUtil.getUserId(authentication), name);
        return GeneralOperationResult.success(entity);
    }

    @GetMapping
    public GeneralOperationResult<List<UserSecretKeyEntity>> readAll(Authentication authentication) {
        // 不返回默认secret
        return GeneralOperationResult.success(repository.findByUserId(securityUtil.getUserId(authentication)).stream().filter(k -> !k.getDefaultKey()).toList());
    }

    @GetMapping("{id}")
    public GeneralOperationResult<UserSecretKeyEntity> read(Authentication authentication, @PathVariable Integer id) {
        Optional<UserSecretKeyEntity> entityOp = repository.findById(id);
        if (entityOp.isEmpty()) {
            return GeneralOperationResult.failureWithNotFound();
        }
        if (!entityOp.get().getUserId().equals(securityUtil.getUserId(authentication))) {
            return GeneralOperationResult.failureWithNotFound();
        }
        // 默认secret不能进行此操作
        if (entityOp.get().getDefaultKey()) {
            return GeneralOperationResult.failureWithNotFound();
        }
        return GeneralOperationResult.success(entityOp.get());
    }

    // 更新UserSecretKeyEntity
    @Transactional
    @PutMapping("{id}")
    public GeneralOperationResult<UserSecretKeyEntity> update(Authentication authentication, @PathVariable Integer id, String name) {
        Optional<UserSecretKeyEntity> entityOp = repository.findById(id);
        if (entityOp.isEmpty()) {
            return GeneralOperationResult.failureWithNotFound();
        }
        if (!entityOp.get().getUserId().equals(securityUtil.getUserId(authentication))) {
            return GeneralOperationResult.failureWithNotFound();
        }
        // 默认secret不能进行此操作
        if (entityOp.get().getDefaultKey()) {
            return GeneralOperationResult.failureWithNotFound();
        }
        UserSecretKeyEntity entity = entityOp.get();
        if (!StringUtils.isBlank(name)) {
            entity.setName(name);
            repository.save(entity);
        }
        return GeneralOperationResult.success(entity);
    }

    @Transactional
    @DeleteMapping("{id}")
    public GeneralOperationResult<?> delete(Authentication authentication, @PathVariable Integer id) {
        Optional<UserSecretKeyEntity> entityOp = repository.findById(id);
        if (entityOp.isEmpty()) {
            return GeneralOperationResult.failureWithNotFound();
        }
        if (!entityOp.get().getUserId().equals(securityUtil.getUserId(authentication))) {
            return GeneralOperationResult.failureWithNotFound();
        }

        // 默认secret不能进行此操作
        if (entityOp.get().getDefaultKey()) {
            return GeneralOperationResult.failureWithNotFound();
        }
        repository.delete(entityOp.get());
        return GeneralOperationResult.success();
    }
}

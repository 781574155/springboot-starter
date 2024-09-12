package com.openai36.aggregation.app.admin;

import com.openai36.aggregation.common.GeneralOperationResult;
import com.openai36.aggregation.eao.UserEntity;
import com.openai36.aggregation.eao.UserRepository;
import com.openai36.aggregation.security.Admin;
import com.openai36.aggregation.service.UserService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("admin/users")
@Admin
public class UserAdminResource {
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserService userService;

    @GetMapping
    public GeneralOperationResult<Page<UserEntity>> getAll(@RequestParam(required = false) String username, Pageable pageable) {
        UserEntity userSample = new UserEntity();
        ExampleMatcher matcher = ExampleMatcher.matchingAll();
        if (username != null && !username.isEmpty()) {
            userSample.setUsername(username.trim());
            matcher = ExampleMatcher.matchingAll().withMatcher("username",ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        }
        Example<UserEntity> example = Example.of(userSample, matcher);

        Page<UserEntity> result = userRepository.findAll(example, pageable);
        result.forEach(e -> e.setPassword(null));
        return GeneralOperationResult.success(result);
    }


    @Transactional
    @DeleteMapping({"{userId}"})
    public GeneralOperationResult<?> delete(@PathVariable Integer userId) {
        Optional<UserEntity> ceOp = userRepository.findById(userId);
        if (ceOp.isEmpty()) {
            return GeneralOperationResult.failureWithNotFound();
        }

        return userService.deleteUser(userId);
    }
}

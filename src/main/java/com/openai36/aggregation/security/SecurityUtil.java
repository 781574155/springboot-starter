package com.openai36.aggregation.security;

import com.openai36.aggregation.eao.UserEntity;
import com.openai36.aggregation.eao.UserRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Singleton
public class SecurityUtil {
    @Inject
    private UserRepository userRepository;

    public Integer getUserId(Authentication authentication) {
        return getUserEntity(authentication).getId();
    }

    /**
     * @param authentication 对于rest接口中注入的Authentication，
     *                       如果是登录后基于session的认证，则authentication.getPrincipal()得到的是MyUserDetails
     *                       如果是登录后基于oauth2的access_token的认证，则authentication.getPrincipal()得到的是org.springframework.security.oauth2.jwt.Jwt
     *
     * @return
     */
    public UserEntity getUserEntity(Authentication authentication) {
        Optional<UserEntity> userOp = userRepository.findByUsername(authentication.getName());
        if (userOp.isPresent()) {
            return userOp.get();
        }
        throw new BadCredentialsException("未找到用户，authentication.getName()="+authentication.getName());
    }
}

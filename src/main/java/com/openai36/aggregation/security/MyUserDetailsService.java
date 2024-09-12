package com.openai36.aggregation.security;

import com.openai36.aggregation.service.dto.WechatLogin;
import com.openai36.aggregation.service.WechatLoginCache;
import com.openai36.aggregation.eao.UserEntity;
import com.openai36.aggregation.eao.UserRepository;
import com.openai36.aggregation.eao.UserRoleEntity;
import com.openai36.aggregation.eao.UserRoleRepository;
import jakarta.inject.Inject;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService{
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserRoleRepository userRoleRepository;
    @Inject
    private WechatLoginCache cache;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> ueOp = userRepository.findByUsername(username);
        if (ueOp.isPresent()) {
            UserEntity ue = ueOp.get();
            List<UserRoleEntity> roles = userRoleRepository.findByUserId(ue.getId());
            return new MyUserDetails(ue, roles);
        }

        WechatLogin wechatLogin = cache.get(username);
        if (wechatLogin != null && wechatLogin.getUserId() != null) {
            ueOp = userRepository.findById(wechatLogin.getUserId());
            if (ueOp.isPresent()) {
                UserEntity ue = ueOp.get();
                List<UserRoleEntity> roles = userRoleRepository.findByUserId(ue.getId());
                UserEntity newUe = new UserEntity();
                newUe.setId(ue.getId());
                newUe.setUsername(ue.getUsername());
                newUe.setPassword(wechatLogin.getTmpPassword());
                newUe.setEnabled(ue.getEnabled());

                cache.remove(username);

                return new MyUserDetails(newUe, roles);
            }
        }

        throw new UsernameNotFoundException(username);
    }
}

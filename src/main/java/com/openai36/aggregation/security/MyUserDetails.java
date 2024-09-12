package com.openai36.aggregation.security;

import com.openai36.aggregation.eao.UserEntity;
import com.openai36.aggregation.eao.UserRoleEntity;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@ToString
@AllArgsConstructor
public class MyUserDetails implements UserDetails {
    private UserEntity userEntity;
    private List<UserRoleEntity> userRoleEntities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoleEntities.stream().map(e -> new SimpleGrantedAuthority(e.getRole())).toList();
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getEnabled();
    }

    public Integer getUserId() {
        return userEntity.getId();
    }
}

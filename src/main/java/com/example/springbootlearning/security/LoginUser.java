package com.example.springbootlearning.security;

import com.example.springbootlearning.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class LoginUser implements UserDetails {

    private final User user; // 持有你的业务实体

    public LoginUser(User user) {
        this.user = user;
    }

    // ★★★ 这里是获取权限的地方（从数据库的 role 字段转换）★★★
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 假设 user.getRole() 返回 "ADMIN" 或 "USER"
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // ★★★ 扩展方法：方便在 Filter 或 Controller 里获取 userId ★★★
    public Long getUserId() {
        return user.getId();
    }

    // 下面这几个一般返回 true（账户未过期、未锁定、凭证未过期、启用）
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
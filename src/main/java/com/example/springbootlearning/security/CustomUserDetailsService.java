
package com.example.springbootlearning.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springbootlearning.entity.User;
import com.example.springbootlearning.mapper.UserMapper;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public CustomUserDetailsService( UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        

        if (user == null) {
            throw new UsernameNotFoundException(
                    "用户名或密码错误"
            );
        }

        // return org.springframework.security.core.userdetails.User
        //         .withUsername(user.getUsername())
        //         .password(user.getPassword())
        //         .roles("USER")
        //         .build();
        return new LoginUser(user);
    }
}
package com.example.springbootlearning.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springbootlearning.dto.LoginRequest;
import com.example.springbootlearning.dto.RegisterRequest;
import com.example.springbootlearning.entity.User;
import com.example.springbootlearning.mapper.UserMapper;
import com.example.springbootlearning.security.JwtService;
import com.example.springbootlearning.vo.LoginResponse;

@Service
public class AuthService {
    // 注册
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    // 登录
    private final AuthenticationManager authenticationManager;
        // 登录:JWT
    private final JwtService jwtService;

    public AuthService(
        PasswordEncoder passwordEncoder,
        UserMapper userMapper,
        AuthenticationManager authenticationManager,
        JwtService jwtService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService=jwtService;
    }

    // 注册
    // 现在已经从controller里面拿到了这个request dto.包含username还有password.
    public void register(RegisterRequest request) {

        // 1. 检查用户名是否已经存在
        // 使用你已经掌握的 MyBatis-Plus 查询即可

        // 2. 创建 Entity
        User user = new User();

        user.setUsername(
                request.getUsername()
        );

        // 3. 密码加密
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );
        // 3.5
        user.setRole("USER");

        // 4. 保存数据库
        userMapper.insert(user);
    }

    // 登录
    public LoginResponse login( //public void login(
        LoginRequest request
    ) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        String token =jwtService.generateToken(request.getUsername());
        
        return new LoginResponse(token);
    }
    

}
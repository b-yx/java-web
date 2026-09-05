package com.example.springbootlearning.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootlearning.common.Result;
import com.example.springbootlearning.dto.LoginRequest;
import com.example.springbootlearning.dto.RegisterRequest;
import com.example.springbootlearning.service.AuthService;
import com.example.springbootlearning.vo.LoginResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<Void> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        authService.register(request);

        return Result.success(null);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response =
                authService.login(request);

        return Result.success(response);
    }
}
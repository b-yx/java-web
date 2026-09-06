package com.example.springbootlearning.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
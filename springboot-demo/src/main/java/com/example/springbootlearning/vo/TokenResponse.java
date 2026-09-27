package com.example.springbootlearning.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken; // 如果开启了 Rotation，这里返回新的 Refresh Token
}
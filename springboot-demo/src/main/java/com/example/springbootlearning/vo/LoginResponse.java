package com.example.springbootlearning.vo;

public class LoginResponse {

    private String token;

    // 以后可以扩展：
    // private String tokenType;
    // private Long expiresIn;
    
    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
package com.example.springbootlearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(
            min = 3,
            max = 20,
            message = "用户名长度必须为 3-20 个字符"
    )
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(
            min = 6,
            max = 100,
            message = "密码长度不能少于 6 位"
    )
    private String password;

    // public String getUsername() {
    //     return username;
    // }

    // public void setUsername(String username) {
    //     this.username = username;
    // }

    // public String getPassword() {
    //     return password;
    // }

    // public void setPassword(String password) {
    //     this.password = password;
    // }
}
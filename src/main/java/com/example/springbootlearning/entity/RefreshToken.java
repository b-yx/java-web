package com.example.springbootlearning.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("sys_refresh_token")
public class RefreshToken {
    @TableId
    private Long id;

    private Long userId;

    private String token;

    private LocalDateTime expiresAt;

    private Boolean revoked;

    private LocalDateTime createdAt;
}
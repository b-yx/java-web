package com.example.springbootlearning.service;

import org.springframework.stereotype.Service;

import com.example.springbootlearning.entity.User;
import com.example.springbootlearning.exception.UserNotFoundException;

@Service
public class UserService {

    public String getUser() {
        return "这是来自 UserService 的用户信息";
    }
    
    public User getUserById(Long id) {
        // 暂时模拟数据库查询
        if (id == 0) {      // 实际上是用户不存在
            // throw new RuntimeException("用户不存在");
            throw new UserNotFoundException(  "用户不存在");
        }

        return new User(
                id,
                "usertom",
                "123456",
                "User"
        );
    }
    
}

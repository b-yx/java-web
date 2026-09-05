package com.example.springbootlearning.exception;

public class UserNotFoundException extends RuntimeException { //UserNotFoundException是：RuntimeException的子类
    public UserNotFoundException(String message) {
        super(message);
    }
}
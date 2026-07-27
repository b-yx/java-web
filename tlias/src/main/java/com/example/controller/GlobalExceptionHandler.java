package com.example.controller;

import com.example.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice  // 标识这是一个全局异常处理器
public class GlobalExceptionHandler {

    // 捕获所有 Exception 类型的异常
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        // 打印详细错误日志（方便排查问题）
        log.error("系统异常: ", e);
        // 返回统一格式的错误信息（不暴露具体错误细节给前端）
        return Result.error("操作失败，请联系管理员");
    }
}
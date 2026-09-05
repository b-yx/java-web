package com.example.springbootlearning.exception;

import com.example.springbootlearning.common.Result;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice   //一个专门处理 Controller 异常的全局组件,当 Controller 请求过程中发生异常时，Spring 可以找到
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)   //这个方法专门处理 `RuntimeException`
    public Result<Void> handleRuntimeException (RuntimeException e) {
        return Result.error(
                500,
                e.getMessage()
        );
    }
       
    //给自定义异常创建处理器
    @ExceptionHandler(UserNotFoundException.class)
    public Result<Void> handleUserNotFoundException(
            UserNotFoundException e
    ) {
        return Result.error(
                404,
                e.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(
            MethodArgumentNotValidException e
    ) {

        String message =
                e.getBindingResult()
                 .getFieldError()
                 .getDefaultMessage();

        return Result.error(
                400,
                message
        );
    }
}
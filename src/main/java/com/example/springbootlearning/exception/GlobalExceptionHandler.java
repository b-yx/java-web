package com.example.springbootlearning.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.springbootlearning.common.Result;

@RestControllerAdvice   //一个专门处理 Controller 异常的全局组件,当 Controller 请求过程中发生异常时，Spring 可以找到
public class GlobalExceptionHandler {

    // 业务异常：HTTP 状态码 = 异常携带的 code（如 409 / 401）
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(e.getCode())
                .body(Result.error(e.getCode(), e.getMessage()));
    }

    // 用户不存在
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Result<Void>> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(404)
                .body(Result.error(404, e.getMessage()));
    }

    // 参数校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.status(400)
                .body(Result.error(400, message));
    }

    // 登录认证失败（用户名或密码错误）：统一文案，不透出框架的 "Bad credentials"
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(401)
                .body(Result.error(401, "用户名或密码错误"));
    }

    // 数据库唯一键冲突（users.username 唯一索引的并发兜底）
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> handleDuplicateKeyException(DuplicateKeyException e) {
        return ResponseEntity.status(409)
                .body(Result.error(409, "用户名已存在"));
    }

    // 兜底
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Void>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(500)
                .body(Result.error(500, e.getMessage()));
    }
}

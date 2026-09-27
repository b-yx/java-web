package com.example.springbootlearning.exception;

/**
 * 业务异常：携带 HTTP 语义状态码，由 GlobalExceptionHandler 统一转换为
 * 对应状态码的 Result 响应。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(500, message);
    }

    public int getCode() {
        return code;
    }
}

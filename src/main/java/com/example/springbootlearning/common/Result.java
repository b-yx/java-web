package com.example.springbootlearning.common;

public class Result<T> {

    private Integer code;

    private String message;

    private T data; // 泛型T

    //无参构造
    public Result() { 
    }
    //有参构造
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    //静态方法  成功方法
    public static <T> Result<T> success(T data) {

        return new Result<>(
                200,
                "success",
                data
        );
    }
    //失败方法
    public static <T> Result<T> error(
            Integer code,
            String message
    ) {

        return new Result<>(
                code,
                message,
                null
        );
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
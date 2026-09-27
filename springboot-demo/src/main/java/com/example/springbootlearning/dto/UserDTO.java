package com.example.springbootlearning.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank(message = "用户名不能为空")   
    private String name;

    @Min(value = 0, message = "年龄不能小于 0")
    private Integer age;  
}

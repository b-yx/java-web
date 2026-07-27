package com.example.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class EmpQueryParam {
    private Integer page = 1;          // 页码，默认1
    private Integer pageSize = 10;     // 每页条数，默认10
    private String name;              // 姓名（模糊查询）
    private Integer gender;           // 性别（精确查询）
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin;          // 入职开始日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;            // 入职结束日期
}

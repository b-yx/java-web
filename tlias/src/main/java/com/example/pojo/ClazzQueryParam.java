package com.example.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ClazzQueryParam {
    private Integer page = 1;          // 页码，默认1
    private Integer pageSize = 10;     // 每页条数，默认10
    private String name;              // 班级名称（模糊查询）
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin;          // 开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;            // 结束时间
}

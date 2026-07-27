package com.example.pojo;

import lombok.Data;

@Data
public class StudentQueryParam {
    private Integer page = 1;          // 页码，默认1
    private Integer pageSize = 10;     // 每页条数，默认10
    private String name;              // 姓名（模糊查询）
    private Integer degree;           // 学历
    private Integer clazzId;          // 班级ID
}

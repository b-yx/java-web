package com.example.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Emp {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private Integer gender;          // 1男 2女
    private String phone;
    private Integer job;             // 1班主任 2讲师 3学工主管 4教研主管 5咨询师
    private Integer salary;
    private String image;
    private LocalDate entryDate;     // 注意类型为 LocalDate
    private Integer deptId;          // 部门ID（逻辑外键）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 扩展字段：用于连表查询时存储部门名称
    private String deptName;

    // 封装员工工作经历信息
    private List<EmpExpr> exprList;
}
package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Integer id;
    private String name;
    private String no;             // 学号
    private Integer gender;        // 1男 2女
    private String phone;
    private String idCard;
    private Integer isCollege;     // 1是 0否
    private String address;
    private Integer degree;        // 1-6
    private LocalDate graduationDate;
    private Integer clazzId;       // 班级ID
    private Short violationCount;  // 违纪次数
    private Short violationScore;  // 违纪扣分
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 扩展字段（不在表中）
    private String clazzName;      // 班级名称
}

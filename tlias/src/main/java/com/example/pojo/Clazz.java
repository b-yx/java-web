package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clazz {
    private Integer id;
    private String name;
    private String room;
    private LocalDate beginDate;
    private LocalDate endDate;
    private Integer masterId;      // 班主任ID（关联 emp.id）
    private Integer subject;       // 学科 1-6
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 扩展字段（不在表中，用于关联查询）
    private String masterName;     // 班主任姓名
    private String status;         // 班级状态：未开班/在读中/已结课
}

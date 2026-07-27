package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCountOption {
    private List<Object> clazzList;   // 班级名称列表
    private List<Object> dataList;    // 对应的人数列表
}

package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOption {
    private List<Object> jobList;   // 职位名称列表（X轴）
    private List<Object> dataList;  // 对应的人数（Y轴）
}
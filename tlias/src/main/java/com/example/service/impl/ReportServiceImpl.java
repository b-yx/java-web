package com.example.service.impl;

import com.example.mapper.EmpMapper;
import com.example.mapper.StudentMapper;
import com.example.pojo.JobOption;
import com.example.pojo.StudentCountOption;
import com.example.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private EmpMapper empMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public JobOption getEmpJobData() {
        // 查询结果：List<Map>，每个Map包含 {pos: "班主任", total: 5}
        List<Map<String, Object>> list = empMapper.countEmpJobData();

        // 提取职位名称列表
        List<Object> jobList = list.stream()
                .map(map -> map.get("pos"))
                .collect(Collectors.toList());

        // 提取人数列表
        List<Object> dataList = list.stream()
                .map(map -> map.get("total"))
                .collect(Collectors.toList());

        return new JobOption(jobList, dataList);
    }

    @Override
    public List<Map<String, Object>> getEmpGenderData() {
        return empMapper.countEmpGenderData();
    }

    @Override
    public List<Map<String, Object>> getStudentDegreeData() {
        return studentMapper.countStudentDegreeData();
    }

    @Override
    public StudentCountOption getStudentCountData() {
        // 查询结果：List<Map>，每个Map包含 {clazzName: "Java就业100期", studentCount: 77}
        List<Map<String, Object>> list = studentMapper.countStudentCountData();

        // 提取班级名称列表
        List<Object> clazzList = list.stream()
                .map(map -> map.get("clazzName"))
                .collect(Collectors.toList());

        // 提取人数列表
        List<Object> dataList = list.stream()
                .map(map -> map.get("studentCount"))
                .collect(Collectors.toList());

        return new StudentCountOption(clazzList, dataList);
    }
}

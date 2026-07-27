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
        List<Map<String, Object>> list = empMapper.countEmpJobData();
        List<Object> jobList = list.stream()
                .map(map -> map.get("pos"))
                .collect(Collectors.toList());
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
        List<Map<String, Object>> list = studentMapper.countStudentCountData();
        List<Object> clazzList = list.stream()
                .map(map -> map.get("clazzName"))
                .collect(Collectors.toList());
        List<Object> dataList = list.stream()
                .map(map -> map.get("studentCount"))
                .collect(Collectors.toList());
        return new StudentCountOption(clazzList, dataList);
    }
}

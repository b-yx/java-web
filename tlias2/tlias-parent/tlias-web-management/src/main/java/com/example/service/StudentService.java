package com.example.service;

import com.example.pojo.PageResult;
import com.example.pojo.Student;
import com.example.pojo.StudentQueryParam;

import java.util.List;
import java.util.Map;

public interface StudentService {
    PageResult page(StudentQueryParam param);
    void deleteByIds(List<Integer> ids);
    void save(Student student);
    Student getById(Integer id);
    void update(Student student);
    void violation(Integer id, Integer score);
    List<Map<String, Object>> getStudentDegreeData();
    List<Map<String, Object>> getStudentCountData();
}

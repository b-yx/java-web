package com.example.service;

import com.example.pojo.JobOption;
import com.example.pojo.StudentCountOption;

import java.util.List;
import java.util.Map;

public interface ReportService {
    JobOption getEmpJobData();

    List<Map<String, Object>> getEmpGenderData();

    /** 统计学员学历信息 */
    List<Map<String, Object>> getStudentDegreeData();

    /** 统计每个班级的学员人数 */
    StudentCountOption getStudentCountData();
}
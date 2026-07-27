package com.example.service;

import com.example.pojo.PageResult;
import com.example.pojo.Student;
import com.example.pojo.StudentQueryParam;

import java.util.List;
import java.util.Map;

public interface StudentService {

    /** 学员列表条件分页查询 */
    PageResult page(StudentQueryParam param);

    /** 批量删除学员 */
    void deleteByIds(List<Integer> ids);

    /** 新增学员 */
    void save(Student student);

    /** 根据ID查询学员 */
    Student getById(Integer id);

    /** 修改学员 */
    void update(Student student);

    /** 违纪处理 */
    void violation(Integer id, Integer score);

    /** 统计学员学历信息 */
    List<Map<String, Object>> getStudentDegreeData();

    /** 统计每个班级的学员人数 */
    List<Map<String, Object>> getStudentCountData();
}

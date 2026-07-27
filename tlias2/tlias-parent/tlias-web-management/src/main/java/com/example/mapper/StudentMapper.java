package com.example.mapper;

import com.example.pojo.Student;
import com.example.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {
    List<Student> list(StudentQueryParam param);
    void deleteByIds(List<Integer> ids);
    void insert(Student student);
    Student getById(Integer id);
    void updateById(Student student);
    void updateViolation(@Param("id") Integer id, @Param("score") Integer score);
    List<Map<String, Object>> countStudentDegreeData();
    List<Map<String, Object>> countStudentCountData();
}

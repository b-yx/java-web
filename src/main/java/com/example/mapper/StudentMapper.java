package com.example.mapper;

import com.example.pojo.Student;
import com.example.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {

    /** 学员列表条件分页查询 */
    List<Student> list(StudentQueryParam param);

    /** 批量删除学员 */
    void deleteByIds(List<Integer> ids);

    /** 新增学员 */
    void insert(Student student);

    /** 根据ID查询学员 */
    Student getById(Integer id);

    /** 更新学员信息 */
    void updateById(Student student);

    /** 违纪处理 */
    void updateViolation(@Param("id") Integer id, @Param("score") Integer score);

    /** 统计学员学历信息 */
    List<Map<String, Object>> countStudentDegreeData();

    /** 统计每个班级的学员人数 */
    List<Map<String, Object>> countStudentCountData();
}

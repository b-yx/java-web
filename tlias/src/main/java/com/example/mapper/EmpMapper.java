package com.example.mapper;

import com.example.pojo.Emp;
import com.example.pojo.EmpQueryParam;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmpMapper {
//    @Select("select e.*, d.name as deptName from emp e left join dept d on " +
//            "e.dept_id = d.id")
    //有分页查询员工
    List<Emp> list(EmpQueryParam param);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO emp(username, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time) " +
            "VALUES (#{username}, #{name}, #{gender}, #{phone}, #{job}, #{salary}, #{image}, #{entryDate}, #{deptId}, #{createTime}, #{updateTime})")
    void insert(Emp emp);

    void deleteByIds(List<Integer> ids);
    Emp getById(Integer id);
    void updateById(Emp emp);


    @MapKey("pos")
    List<Map<String, Object>> countEmpJobData();

    List<Map<String, Object>> countEmpGenderData();

    // login
    // 根据用户名和密码查询员工（注意：实际开发中密码要加密，这里先明文）
    @Select("SELECT * FROM emp WHERE username = #{username} AND password = #{password}")
    Emp getByUsernameAndPassword(Emp emp);

}

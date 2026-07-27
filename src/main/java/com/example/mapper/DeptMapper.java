package com.example.mapper;

import com.example.pojo.Dept;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeptMapper {
    @Select("SELECT * FROM dept")
    List<Dept> findAll();
    // 根据 ID 删除部门
    @Delete("DELETE FROM dept WHERE id = #{id}")
    void deleteById(Integer id);

    // 新增部门（主键自动回填，不需要手动设置 id）
    @Insert("INSERT INTO dept(name, create_time, update_time) VALUES(#{name}, #{createTime}, #{updateTime})")
    void insert(Dept dept);

    // 根据 ID 查询
    @Select("SELECT * FROM dept WHERE id = #{id}")
    Dept getById(Integer id);

    // 修改部门
    @Update("UPDATE dept SET name = #{name}, update_time = #{updateTime} WHERE id = #{id}")
    void update(Dept dept);
}

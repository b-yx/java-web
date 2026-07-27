package com.example.mapper;

import com.example.pojo.Clazz;
import com.example.pojo.ClazzQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClazzMapper {

    /** 班级列表条件分页查询 */
    List<Clazz> list(ClazzQueryParam param);

    /** 根据ID删除班级 */
    void deleteById(Integer id);

    /** 新增班级 */
    void insert(Clazz clazz);

    /** 根据ID查询班级 */
    Clazz getById(Integer id);

    /** 更新班级信息 */
    void updateById(Clazz clazz);

    /** 查询所有班级 */
    List<Clazz> listAll();
}

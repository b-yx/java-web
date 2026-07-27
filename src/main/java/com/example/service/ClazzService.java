package com.example.service;

import com.example.pojo.Clazz;
import com.example.pojo.ClazzQueryParam;
import com.example.pojo.PageResult;

import java.util.List;

public interface ClazzService {

    /** 班级列表条件分页查询 */
    PageResult page(ClazzQueryParam param);

    /** 根据ID删除班级 */
    void deleteById(Integer id);

    /** 新增班级 */
    void save(Clazz clazz);

    /** 根据ID查询班级 */
    Clazz getById(Integer id);

    /** 修改班级 */
    void update(Clazz clazz);

    /** 查询所有班级 */
    List<Clazz> listAll();
}

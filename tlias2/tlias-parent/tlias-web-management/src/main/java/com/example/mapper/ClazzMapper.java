package com.example.mapper;

import com.example.pojo.Clazz;
import com.example.pojo.ClazzQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClazzMapper {
    List<Clazz> list(ClazzQueryParam param);
    void deleteById(Integer id);
    void insert(Clazz clazz);
    Clazz getById(Integer id);
    void updateById(Clazz clazz);
    List<Clazz> listAll();
}

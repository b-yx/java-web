package com.example.service;

import com.example.pojo.Clazz;
import com.example.pojo.ClazzQueryParam;
import com.example.pojo.PageResult;

import java.util.List;

public interface ClazzService {
    PageResult page(ClazzQueryParam param);
    void deleteById(Integer id);
    void save(Clazz clazz);
    Clazz getById(Integer id);
    void update(Clazz clazz);
    List<Clazz> listAll();
}

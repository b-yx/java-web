package com.example.service;

import com.example.pojo.Emp;
import com.example.pojo.EmpQueryParam;
import com.example.pojo.LoginInfo;
import com.example.pojo.PageResult;

import java.util.List;

public interface EmpService {
    PageResult page(EmpQueryParam param);
    void save(Emp emp);
    void deleteByIds(List<Integer> ids);
    Emp getInfo(Integer id);
    void update(Emp emp);
    LoginInfo login(Emp emp);
}

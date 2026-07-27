package com.example.mapper;

import com.example.pojo.EmpLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmpLogMapper {
    @Insert("INSERT INTO emp_log (operate_time, info) VALUES (#{operateTime}, #{info})")
    void insert(EmpLog empLog);
}

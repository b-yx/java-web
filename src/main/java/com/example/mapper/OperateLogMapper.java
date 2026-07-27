package com.example.mapper;

import com.example.pojo.OperateLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperateLogMapper {

    @Insert("INSERT INTO operate_log (operate_emp_id, operate_time, class_name, method_name, method_params, return_value, cost_time) " +
            "VALUES (#{operateEmpId}, #{operateTime}, #{className}, #{methodName}, #{methodParams}, #{returnValue}, #{costTime})")
    void insert(OperateLog operateLog);

    /** 分页查询操作日志（关联emp表获取操作人姓名） */
    @Select("SELECT ol.*, e.name AS operate_emp_name " +
            "FROM operate_log ol " +
            "LEFT JOIN emp e ON ol.operate_emp_id = e.id " +
            "ORDER BY ol.operate_time DESC")
    List<OperateLog> page();
}

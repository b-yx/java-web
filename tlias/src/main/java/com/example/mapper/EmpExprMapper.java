package com.example.mapper;

import com.example.pojo.EmpExpr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmpExprMapper {
    // 批量插入工作经历
    void insertBatch(@Param("exprList") List<EmpExpr> exprList);

    void deleteByEmpIds(List<Integer> empIds);
}
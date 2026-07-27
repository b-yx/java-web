package com.example.service.impl;

import com.example.mapper.EmpExprMapper;
import com.example.mapper.EmpMapper;
import com.example.pojo.*;
import com.example.service.EmpLogService;
import com.example.service.EmpService;
import com.example.utils.JwtUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private EmpExprMapper empExprMapper;
    @Autowired
    private EmpLogService empLogService;

    @Override
    public PageResult page(EmpQueryParam empQueryParam) {
        // 1. 设置分页参数（PageHelper 会拦截后面的第一次查询）
        PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());

        // 2. 执行查询（此时会触发分页拦截，自动拼接 limit 并先查询 count）
        Page<Emp> page = (Page<Emp>) empMapper.list(empQueryParam);

        // 3. 封装分页结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Emp emp) {
        try {
            // 1. 补全基础属性（创建时间和修改时间）
            emp.setCreateTime(LocalDateTime.now());
            emp.setUpdateTime(LocalDateTime.now());

            // 2. 保存员工基本信息（插入 emp 表）
            empMapper.insert(emp);  // 执行后，emp.getId() 就有值了

            // 3. 保存员工的工作经历信息（批量插入 emp_expr 表）
            Integer empId = emp.getId();
            List<EmpExpr> exprList = emp.getExprList();
            if (!CollectionUtils.isEmpty(exprList)) {
                // 为每一段工作经历设置所属的员工ID
                exprList.forEach(expr -> expr.setEmpId(empId));
                empExprMapper.insertBatch(exprList);
            }
        }finally {
            // 记录操作日志（无论成功失败都记录）
            EmpLog empLog = new EmpLog(null, LocalDateTime.now(), emp.toString());
            empLogService.insertLog(empLog);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIds(List<Integer> ids) {
        // 1. 批量删除员工基本信息（emp表）
        empMapper.deleteByIds(ids);

        // 2. 批量删除员工的工作经历信息（emp_expr表）
        empExprMapper.deleteByEmpIds(ids);
    }
    @Override
    public Emp getInfo(Integer id) {
        return empMapper.getById(id);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Emp emp) {
        // 1. 更新员工基本信息（补全修改时间）
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);

        // 2. 删除该员工所有旧的工作经历
        empExprMapper.deleteByEmpIds(Arrays.asList(emp.getId()));

        // 3. 插入新的工作经历列表
        List<EmpExpr> exprList = emp.getExprList();
        if (!CollectionUtils.isEmpty(exprList)) {
            // 为每条工作经历设置所属员工ID
            exprList.forEach(expr -> expr.setEmpId(emp.getId()));
            empExprMapper.insertBatch(exprList);
        }
    }
    //login
    @Override
    public LoginInfo login(Emp emp) {
        // 1. 根据用户名和密码查询数据库
        Emp loginEmp = empMapper.getByUsernameAndPassword(emp);

        // 2. 判断是否查询到数据
        if (loginEmp != null) {
            // 3. 登录成功，生成 JWT 令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginEmp.getId());
            claims.put("username", loginEmp.getUsername());

            String token = JwtUtils.generateJwt(claims);

            // 4. 封装返回数据
            return new LoginInfo(loginEmp.getId(), loginEmp.getUsername(), loginEmp.getName(), token);
        }
        // 5. 登录失败，返回 null
        return null;
    }
}
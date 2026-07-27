package com.example.controller;

import com.example.mapper.OperateLogMapper;
import com.example.pojo.OperateLog;
import com.example.pojo.PageResult;
import com.example.pojo.Result;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private OperateLogMapper operateLogMapper;

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("日志分页查询，page: {}, pageSize: {}", page, pageSize);
        PageHelper.startPage(page, pageSize);
        Page<OperateLog> pageResult = (Page<OperateLog>) operateLogMapper.page();
        return Result.success(new PageResult(pageResult.getTotal(), pageResult.getResult()));
    }
}

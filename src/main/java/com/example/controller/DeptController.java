package com.example.controller;


import com.example.anno.LogOperation;
import com.example.pojo.Dept;
import com.example.pojo.Result;
import com.example.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/depts")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @LogOperation
    @GetMapping
    public Result list(){
        log.info("查询部门列表");
        List<Dept> deptList = deptService.findAll();
        return Result.success(deptList);
    }
    @LogOperation
    @DeleteMapping
    public Result delete(Integer id) {   // 参数名与前端传递的 ?id=xxx 一致
        log.info("根据id 删除部门，id: {}",id);
        deptService.deleteById(id);
        return Result.success();
    }
    @LogOperation
    @PostMapping
    public Result save(@RequestBody Dept dept) {   // @RequestBody 接收 JSON 并自动封装
        log.info("新增部门: dept:{}",dept);
        deptService.save(dept);
        return Result.success();
    }
    @LogOperation
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {// @PathVariable 获取路径中的 id
        log.info("根据id查询,id:{}",id);
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }
    @LogOperation
    @PutMapping
    public Result update(@RequestBody Dept dept) {
        log.info("修改部门：的dept:{}",dept);
        deptService.update(dept);
        return Result.success();
    }
}

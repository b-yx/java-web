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

    @GetMapping
    public Result list(){
        log.info("查询部门列表");
        List<Dept> deptList = deptService.findAll();
        return Result.success(deptList);
    }

    @LogOperation
    @DeleteMapping
    public Result delete(Integer id) {
        log.info("根据id 删除部门，id: {}",id);
        deptService.deleteById(id);
        return Result.success();
    }

    @LogOperation
    @PostMapping
    public Result save(@RequestBody Dept dept) {
        log.info("新增部门: dept:{}",dept);
        deptService.save(dept);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
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

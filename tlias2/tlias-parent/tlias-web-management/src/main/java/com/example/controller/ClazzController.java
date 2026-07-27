package com.example.controller;

import com.example.anno.LogOperation;
import com.example.pojo.Clazz;
import com.example.pojo.ClazzQueryParam;
import com.example.pojo.PageResult;
import com.example.pojo.Result;
import com.example.service.ClazzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/clazzs")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    @GetMapping
    public Result page(ClazzQueryParam clazzQueryParam) {
        log.info("班级列表查询参数: {}", clazzQueryParam);
        PageResult pageResult = clazzService.page(clazzQueryParam);
        return Result.success(pageResult);
    }

    @LogOperation
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        log.info("删除班级，id: {}", id);
        clazzService.deleteById(id);
        return Result.success();
    }

    @LogOperation
    @PostMapping
    public Result save(@RequestBody Clazz clazz) {
        log.info("新增班级: {}", clazz);
        clazzService.save(clazz);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("根据ID查询班级，id: {}", id);
        Clazz clazz = clazzService.getById(id);
        return Result.success(clazz);
    }

    @LogOperation
    @PutMapping
    public Result update(@RequestBody Clazz clazz) {
        log.info("修改班级: {}", clazz);
        clazzService.update(clazz);
        return Result.success();
    }

    @GetMapping("/list")
    public Result listAll() {
        log.info("查询所有班级");
        List<Clazz> clazzList = clazzService.listAll();
        return Result.success(clazzList);
    }
}

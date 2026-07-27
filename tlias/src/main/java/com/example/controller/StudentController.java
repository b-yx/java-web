package com.example.controller;

import com.example.anno.LogOperation;
import com.example.pojo.PageResult;
import com.example.pojo.Result;
import com.example.pojo.Student;
import com.example.pojo.StudentQueryParam;
import com.example.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /** 学员列表条件分页查询 */
    @GetMapping
    public Result page(StudentQueryParam studentQueryParam) {
        log.info("学员列表查询参数: {}", studentQueryParam);
        PageResult pageResult = studentService.page(studentQueryParam);
        return Result.success(pageResult);
    }

    /** 批量删除学员 */
    @LogOperation
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable List<Integer> ids) {
        log.info("批量删除学员，ids: {}", ids);
        studentService.deleteByIds(ids);
        return Result.success();
    }

    /** 新增学员 */
    @LogOperation
    @PostMapping
    public Result save(@RequestBody Student student) {
        log.info("新增学员: {}", student);
        studentService.save(student);
        return Result.success();
    }

    /** 根据ID查询学员 */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("根据ID查询学员，id: {}", id);
        Student student = studentService.getById(id);
        return Result.success(student);
    }

    /** 修改学员 */
    @LogOperation
    @PutMapping
    public Result update(@RequestBody Student student) {
        log.info("修改学员: {}", student);
        studentService.update(student);
        return Result.success();
    }

    /** 违纪处理 */
    @LogOperation
    @PutMapping("/violation/{id}/{score}")
    public Result violation(@PathVariable Integer id, @PathVariable Integer score) {
        log.info("学员违纪处理，id: {}, 扣分: {}", id, score);
        studentService.violation(id, score);
        return Result.success();
    }
}

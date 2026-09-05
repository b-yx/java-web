package com.example.springbootlearning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.springbootlearning.mapper")
public class SpringbootLearningApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                SpringbootLearningApplication.class,
                args
        );

    }

}
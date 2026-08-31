package com.antdigital.todo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 待办事项应用启动类。
 *
 * <p>承载"新增待办事项"最小闭环，单体 Spring Boot 应用。</p>
 */
@SpringBootApplication
@MapperScan("com.antdigital.todo.dao.mapper")
public class TodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }
}

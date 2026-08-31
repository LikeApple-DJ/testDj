package com.antdigital.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 待办事项应用启动类
 *
 * @author AiWork
 * @date 2026/08/31
 */
@SpringBootApplication
public class TodoApplication {

    /**
     * 主入口方法
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }
}

package com.example.employeeboard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 人员看板应用启动类。
 */
@SpringBootApplication
@MapperScan("com.example.employeeboard.**.mapper")
public class EmployeeBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeBoardApplication.class, args);
    }
}

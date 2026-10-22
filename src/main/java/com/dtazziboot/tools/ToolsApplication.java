package com.dtazziboot.tools;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 应用启动入口。
 *
 * <p>承载三接口（helloworld / 哈希 / 冒泡排序）、导出、调用埋点与报表能力。</p>
 */
@EnableAsync
@MapperScan("com.dtazziboot.tools.dao.mapper")
@SpringBootApplication
public class ToolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolsApplication.class, args);
    }
}
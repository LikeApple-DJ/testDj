package com.dtazziboot.tools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步埋点线程池配置：埋点落库线程池与主业务线程隔离，队列有界避免无限堆积。
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "invokeLogExecutor")
    public Executor invokeLogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("invoke-log-");
        executor.initialize();
        return executor;
    }
}
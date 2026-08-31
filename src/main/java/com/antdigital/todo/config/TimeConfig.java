package com.antdigital.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * 时间 Clock 配置，提供统一时区的 Clock Bean。
 *
 * <p>所有应用层时间获取均应通过此 Clock，避免 {@code LocalDateTime.now()} 使用
 * 系统默认时区导致多实例部署或 JVM/DB 时区不一致时时间戳偏差。
 * 统一使用 Asia/Shanghai 时区，与 DB 连接 {@code serverTimezone=Asia/Shanghai} 保持一致。</p>
 */
@Configuration
public class TimeConfig {

    /**
     * 统一时区 Clock Bean。
     *
     * @return 固定 Asia/Shanghai 时区的 Clock 实例
     */
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Asia/Shanghai"));
    }
}

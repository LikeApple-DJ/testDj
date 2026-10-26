package com.dt.example.web;

import com.dt.example.metrics.MetricsCollector;
import com.dt.example.metrics.MetricsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：自定义埋点拦截器与跨域策略。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MetricsCollector collector;

    public WebConfig(MetricsCollector collector) {
        this.collector = collector;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MetricsInterceptor(collector))
            .addPathPatterns("/api/helloWorld", "/api/hash", "/api/sort/bubble");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "OPTIONS");
    }
}
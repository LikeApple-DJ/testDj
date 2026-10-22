package com.dtazziboot.tools.config;

import com.dtazziboot.tools.service.InvokeLogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：注册登录态解析拦截器与埋点拦截器。
 */
@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer(InvokeLogService invokeLogService) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new UserContextInterceptor())
                        .addPathPatterns("/api/**");
                registry.addInterceptor(new InvokeTrackerInterceptor(invokeLogService))
                        .addPathPatterns("/api/**");
            }
        };
    }
}
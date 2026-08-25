package com.example.demo.config;

import com.example.demo.interceptor.CallTrackingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private CallTrackingInterceptor callTrackingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Token 认证拦截器（顺序在前）
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");

        // 埋点跟踪拦截器（顺序在后）
        registry.addInterceptor(callTrackingInterceptor)
                .addPathPatterns("/api/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
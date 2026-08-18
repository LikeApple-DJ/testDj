package com.example.demo.config;

import com.example.demo.interceptor.MetricsInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MetricsInterceptor metricsInterceptor;

    @Value("${metrics.enabled:true}")
    private boolean metricsEnabled;

    public WebConfig(MetricsInterceptor metricsInterceptor) {
        this.metricsInterceptor = metricsInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (metricsEnabled) {
            registry.addInterceptor(metricsInterceptor)
                    .addPathPatterns("/api/**")
                    .excludePathPatterns("/api/metrics");
        }
    }
}
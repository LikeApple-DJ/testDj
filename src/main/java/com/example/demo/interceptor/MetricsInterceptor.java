package com.example.demo.interceptor;

import com.example.demo.entity.MetricsRecord;
import com.example.demo.repository.MetricsRecordRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

@Component
public class MetricsInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(MetricsInterceptor.class);

    private final MetricsRecordRepository repository;
    private final ThreadPoolTaskExecutor metricsExecutor;

    public MetricsInterceptor(MetricsRecordRepository repository) {
        this.repository = repository;
        this.metricsExecutor = new ThreadPoolTaskExecutor();
        this.metricsExecutor.setCorePoolSize(2);
        this.metricsExecutor.setMaxPoolSize(4);
        this.metricsExecutor.setQueueCapacity(100);
        this.metricsExecutor.setThreadNamePrefix("metrics-");
        this.metricsExecutor.initialize();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        String callerName = request.getHeader("X-Caller-Name");
        if (callerName == null || callerName.isBlank()) callerName = "anonymous";

        String callerType = request.getHeader("X-Caller-Type");
        if (callerType == null || callerType.isBlank()) callerType = "未知";

        String callerLevel = request.getHeader("X-Caller-Level");
        if (callerLevel == null || callerLevel.isBlank()) callerLevel = "未知";

        String callerDept = request.getHeader("X-Caller-Dept");
        if (callerDept == null || callerDept.isBlank()) callerDept = "未知";

        MetricsRecord record = new MetricsRecord();
        record.setCallerName(callerName);
        record.setCallerType(callerType);
        record.setCallerLevel(callerLevel);
        record.setCallerDept(callerDept);
        record.setApiPath(request.getRequestURI());
        record.setApiMethod(request.getMethod());
        record.setCallTime(LocalDateTime.now(ZoneOffset.UTC));
        record.setClientIp(request.getRemoteAddr());
        record.setUserAgent(request.getHeader("User-Agent"));

        CompletableFuture.runAsync(() -> {
            try {
                repository.save(record);
            } catch (Exception e) {
                log.error("埋点写入失败: apiPath={}, callerName={}", record.getApiPath(), record.getCallerName(), e);
            }
        }, metricsExecutor);

        return true;
    }
}
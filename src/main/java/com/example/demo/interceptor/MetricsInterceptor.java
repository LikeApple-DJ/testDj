package com.example.demo.interceptor;

import com.example.demo.entity.MetricsRecord;
import com.example.demo.repository.MetricsRecordRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
public class MetricsInterceptor implements HandlerInterceptor {

    private final MetricsRecordRepository repository;

    public MetricsInterceptor(MetricsRecordRepository repository) {
        this.repository = repository;
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
        record.setCallTime(LocalDateTime.now());
        record.setClientIp(request.getRemoteAddr());
        record.setUserAgent(request.getHeader("User-Agent"));

        CompletableFuture.runAsync(() -> {
            try {
                repository.save(record);
            } catch (Exception e) {
                // 埋点写入失败仅记日志，不影响业务
                System.err.println("Metrics save failed: " + e.getMessage());
            }
        });

        return true;
    }
}
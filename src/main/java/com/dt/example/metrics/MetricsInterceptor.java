package com.dt.example.metrics;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 对业务接口调用进行埋点：从请求头 X-Caller-Id 获取调用人。
 */
public class MetricsInterceptor implements HandlerInterceptor {

    public static final String CALLER_HEADER = "X-Caller-Id";

    private final MetricsCollector collector;

    public MetricsInterceptor(MetricsCollector collector) {
        this.collector = collector;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (ex != null || response.getStatus() < 200 || response.getStatus() >= 300) {
            return;
        }
        String caller = request.getHeader(CALLER_HEADER);
        if (caller == null || caller.isBlank()) {
            caller = "anonymous";
        }
        collector.record(caller, request.getRequestURI());
    }
}
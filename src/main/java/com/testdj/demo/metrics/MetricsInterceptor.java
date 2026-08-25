package com.testdj.demo.metrics;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.UUID;

@Component
public class MetricsInterceptor implements HandlerInterceptor {

    private final MetricService metricService;

    public MetricsInterceptor(MetricService metricService) {
        this.metricService = metricService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/v1/demo/hello") || uri.startsWith("/api/v1/demo/hash") || uri.startsWith("/api/v1/demo/sort/bubble")) {
            MetricEvent event = new MetricEvent(
                    UUID.randomUUID().toString(),
                    extractUserId(request),
                    request.getHeader("X-User-Type"),
                    request.getHeader("X-User-Level"),
                    request.getHeader("X-User-Dept"),
                    request.getMethod() + " " + uri,
                    Instant.now());
            metricService.track(event);
        }
        return true;
    }

    private String extractUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        return userId == null ? "anonymous" : userId;
    }
}
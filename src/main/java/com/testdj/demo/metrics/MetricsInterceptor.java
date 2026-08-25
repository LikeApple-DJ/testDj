package com.testdj.demo.metrics;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.UUID;

@Component
public class MetricsInterceptor implements HandlerInterceptor {

    private static final String HELLO_PATH = "/api/v1/demo/hello";
    private static final String HASH_PATH = "/api/v1/demo/hash";
    private static final String SORT_BUBBLE_PATH = "/api/v1/demo/sort/bubble";

    private final MetricService metricService;

    public MetricsInterceptor(MetricService metricService) {
        this.metricService = metricService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if (isTrackedUri(uri)) {
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

    private boolean isTrackedUri(String uri) {
        return uri.startsWith(HELLO_PATH)
                || uri.startsWith(HASH_PATH)
                || uri.startsWith(SORT_BUBBLE_PATH);
    }

    private String extractUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        return userId == null ? "anonymous" : userId;
    }
}

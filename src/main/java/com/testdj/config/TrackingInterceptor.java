package com.testdj.config;

import com.testdj.service.TrackingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * Interceptor that automatically logs calls to business APIs (hello, hash, bubble-sort).
 * Tracks every successful request to these endpoints into the api_call_log table.
 */
@Component
public class TrackingInterceptor implements HandlerInterceptor {

    private static final Set<String> TRACKED_PATHS = Set.of(
            "/api/hello",
            "/api/hash",
            "/api/bubble-sort"
    );

    private final TrackingService trackingService;

    public TrackingInterceptor(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        String path = request.getRequestURI();
        if (TRACKED_PATHS.contains(path) && ex == null && response.getStatus() < 400) {
            String apiName = path.substring("/api/".length()); // e.g., "hello", "hash", "bubble-sort"
            String caller = getHeaderOrDefault(request, "X-Caller", "system");
            String department = getHeaderOrDefault(request, "X-Department", "default");
            String level = getHeaderOrDefault(request, "X-Level", "INFO");
            String type = getHeaderOrDefault(request, "X-Type", "auto");

            trackingService.track(apiName, caller, department, level, type);
        }
    }

    private String getHeaderOrDefault(HttpServletRequest request, String headerName, String defaultValue) {
        String value = request.getHeader(headerName);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
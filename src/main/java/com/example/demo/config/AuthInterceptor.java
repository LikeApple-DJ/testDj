package com.example.demo.config;

import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 排除登录接口和 OPTIONS 请求
        String path = request.getRequestURI();
        if (path.equals("/api/auth/login") || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Missing or invalid token\",\"status\":401}");
            return false;
        }

        String token = authHeader.substring(7);
        String callerId = authService.getCallerIdFromToken(token);
        if (callerId == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Invalid or expired token\",\"status\":401}");
            return false;
        }

        // 将 callerId 存入请求属性，供后续拦截器/Controller使用
        request.setAttribute("callerId", callerId);
        return true;
    }
}
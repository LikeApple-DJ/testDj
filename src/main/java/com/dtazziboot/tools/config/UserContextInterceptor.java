package com.dtazziboot.tools.config;

import com.dtazziboot.tools.common.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录态解析拦截器：从请求头解析调用人及人员维度写入 UserContext。
 *
 * <p>沿用现有统一登录态体系时，可替换为既有的用户会话组件；此处以请求头占位实现。</p>
 */
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserContext.set(
                request.getHeader("X-User-Id"),
                request.getHeader("X-User-Name"),
                request.getHeader("X-Person-Type"),
                request.getHeader("X-Person-Level"),
                request.getHeader("X-Person-Dept"));
        return true;
    }
}
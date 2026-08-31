package com.antdigital.todo.config;

import com.antdigital.todo.common.ApiResponse;
import com.antdigital.todo.common.ErrorCode;
import com.antdigital.todo.common.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 登录态校验拦截器。
 *
 * <p>对应 design.md §6.4.2.3：全局统一拦截器校验登录态，未登录返回 TODO_005。</p>
 * <p>当前从请求头 X-Tenant-Id 和 X-User-Id 提取登录态信息（假设复用内部统一登录，
 * 上游网关注入）。生产环境可对接 BUC/SSO Session。</p>
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    private static final String TENANT_ID_HEADER = "X-Tenant-Id";
    private static final String USER_ID_HEADER = "X-User-Id";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        String userId = request.getHeader(USER_ID_HEADER);

        if (tenantId == null || tenantId.isBlank() || userId == null || userId.isBlank()) {
            logger.warn("登录态缺失, path: {}, 返回 TODO_005", request.getRequestURI());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(ErrorCode.TODO_005)));
            return false;
        }

        // 注入登录态到上下文，禁止客户端传入 tenant_id/creator 到业务层
        UserContext.set(tenantId, userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}

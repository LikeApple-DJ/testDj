package com.example.todo.context;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 默认用户上下文实现：从请求头 {@code X-User-Id} 读取登录态用户ID。
 *
 * <p>登录拦截器校验通过后，将用户ID写入该请求头；本实现仅做读取，
 * 不承担鉴权职责。非 HTTP 线程（如异步任务）下返回 null。</p>
 */
@Component
public class DefaultUserContext implements UserContext {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public Long getCurrentUserId() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            return null;
        }
        String header = servletAttributes.getRequest().getHeader(USER_ID_HEADER);
        if (header == null || header.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(header.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

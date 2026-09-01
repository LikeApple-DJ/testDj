package com.org.module.context;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * {@link UserContext} 默认实现：从当前请求中读取内部网关注入的用户身份头。
 * <p>
 * 约定：内部 oneapi 网关在完成登录态校验后，将内部用户 ID 通过 {@code X-User-Id} 请求头透传给应用。
 * 暂未接入鉴权拦截器时，若无该头则视为未获取到登录用户信息。
 */
@Component
public class UserContextImpl implements UserContext {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public Optional<Long> getCurrentUserId() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return Optional.empty();
        }
        String header = attributes.getRequest().getHeader(USER_ID_HEADER);
        if (header == null || header.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(header.trim()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}

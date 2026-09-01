package com.org.module.context;

import java.util.Optional;

/**
 * 登录上下文，提供当前登录用户身份。
 * <p>
 * 内部用户经 Web 控制台/oneapi 访问，鉴权由全局统一拦截器保障；
 * 本抽象负责从登录上下文中获取当前用户 ID，供业务层注入 creator_id 等字段。
 */
public interface UserContext {

    /**
     * 获取当前登录用户 ID
     *
     * @return 用户 ID；未获取到登录用户信息时返回 {@link Optional#empty()}
     */
    Optional<Long> getCurrentUserId();
}

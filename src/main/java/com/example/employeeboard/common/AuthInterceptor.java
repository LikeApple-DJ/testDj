package com.example.employeeboard.common;

import com.example.employeeboard.whitelist.WhitelistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理端访问拦截器：校验登录态（工号头）→ 白名单校验，不通过返回 WL_001。
 *
 * <p>统一登录态假设：网关在请求头 X-Emp-No 中透传当前用户工号。</p>
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthInterceptor.class);
    private static final String HEADER_EMP_NO = "X-Emp-No";

    private final WhitelistService whitelistService;

    public AuthInterceptor(WhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String empNo = request.getHeader(HEADER_EMP_NO);
        // 先做白名单校验，通过后才写入 ThreadLocal，避免拒绝路径残留上下文
        if (!whitelistService.isAllowed(empNo)) {
            LOGGER.warn("whitelist check rejected, empNo={}", empNo);
            throw new BizException("WL_001", "操作者不在白名单");
        }
        UserContext.setCurrentEmpNo(empNo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}

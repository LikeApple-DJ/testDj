package com.dtazziboot.tools.config;

import com.dtazziboot.tools.common.enums.ApiCodeEnum;
import com.dtazziboot.tools.common.util.UserContext;
import com.dtazziboot.tools.dao.entity.InvokeLogDO;
import com.dtazziboot.tools.service.InvokeLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

/**
 * 调用埋点拦截器：白名单接口统一采集调用次数与调用人，异步落库，不阻塞主链路。
 */
public class InvokeTrackerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(InvokeTrackerInterceptor.class);

    private final InvokeLogService invokeLogService;

    public InvokeTrackerInterceptor(InvokeLogService invokeLogService) {
        this.invokeLogService = invokeLogService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        ApiCodeEnum apiCode = ApiCodeMatcher.match(request.getRequestURI());
        if (apiCode == null) {
            return;
        }
        try {
            InvokeLogDO log = new InvokeLogDO();
            log.setApiCode(apiCode.getCode());
            log.setApiName(apiCode.getName());
            log.setInvokerId(UserContext.getInvokerId());
            log.setInvokerName(UserContext.getInvokerName());
            log.setPersonType(UserContext.getPersonType());
            log.setPersonLevel(UserContext.getPersonLevel());
            log.setPersonDept(UserContext.getPersonDept());
            log.setInvokeTime(LocalDateTime.now());
            log.setResultCode(String.valueOf(response.getStatus()));
            log.setIp(remoteIp(request));
            invokeLogService.record(log);
        } catch (Exception e) {
            logger.warn("埋点采集异常, uri: {}", request.getRequestURI(), e);
        } finally {
            UserContext.clear();
        }
    }

    private String remoteIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static final class ApiCodeMatcher {
        private ApiCodeMatcher() {
        }

        static ApiCodeEnum match(String uri) {
            if (uri == null) {
                return null;
            }
            if (uri.endsWith("/api/tools/helloworld")) {
                return ApiCodeEnum.HELLO;
            }
            if (uri.endsWith("/api/tools/hash")) {
                return ApiCodeEnum.HASH;
            }
            if (uri.endsWith("/api/tools/bubble-sort")) {
                return ApiCodeEnum.BUBBLE_SORT;
            }
            if (uri.endsWith("/api/tools/export")) {
                return ApiCodeEnum.EXPORT;
            }
            if (uri.endsWith("/api/report/trend")) {
                return ApiCodeEnum.REPORT_TREND;
            }
            if (uri.endsWith("/api/report/distribution")) {
                return ApiCodeEnum.REPORT_DISTRIBUTION;
            }
            return null;
        }
    }
}
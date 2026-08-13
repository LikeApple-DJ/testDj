package com.example.demo.aspect;

import com.example.demo.entity.ApiCallLog;
import com.example.demo.repository.ApiCallLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Aspect
@Component
public class ApiCallLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ApiCallLogAspect.class);

    private final ApiCallLogRepository repository;
    private final ObjectMapper objectMapper;

    public ApiCallLogAspect(ApiCallLogRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @AfterReturning(
        pointcut = "execution(* com.example.demo.controller.HelloController.*(..)) || " +
                   "execution(* com.example.demo.controller.HashController.*(..)) || " +
                   "execution(* com.example.demo.controller.BubbleSortController.*(..))",
        returning = "result"
    )
    public void logApiCall(JoinPoint joinPoint, Object result) {
        try {
            ApiCallLog apiCallLog = new ApiCallLog();

            // Determine API name from controller class
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String apiName = mapControllerToApiName(className);
            apiCallLog.setApiName(apiName);

            // Extract user info from headers
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                apiCallLog.setUserId(getHeaderOrDefault(request, "X-User-Id", "anonymous"));
                apiCallLog.setUserType(request.getHeader("X-User-Type"));
                apiCallLog.setUserLevel(request.getHeader("X-User-Level"));
                apiCallLog.setUserDept(request.getHeader("X-User-Dept"));
            } else {
                apiCallLog.setUserId("anonymous");
            }

            apiCallLog.setCallTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));

            // Serialize request body
            try {
                Object[] args = joinPoint.getArgs();
                if (args.length > 0) {
                    apiCallLog.setRequestBody(objectMapper.writeValueAsString(args[0]));
                }
            } catch (Exception e) {
                log.warn("Failed to serialize request body", e);
            }

            // Serialize response body
            try {
                apiCallLog.setResponseBody(objectMapper.writeValueAsString(result));
            } catch (Exception e) {
                log.warn("Failed to serialize response body", e);
            }

            repository.save(apiCallLog);
        } catch (Exception e) {
            log.error("Failed to log API call", e);
            // Do not affect main flow
        }
    }

    private String mapControllerToApiName(String controllerName) {
        if (controllerName.contains("Hello")) return "hello";
        if (controllerName.contains("Hash")) return "hash";
        if (controllerName.contains("BubbleSort")) return "bubble-sort";
        return "unknown";
    }

    private String getHeaderOrDefault(HttpServletRequest request, String header, String defaultValue) {
        String value = request.getHeader(header);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}

package com.example.demo.aspect;
import com.example.demo.model.TrackingRecord;
import com.example.demo.repository.TrackingRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
@Aspect
@Component
public class TrackingAspect {
    private static final Logger log = LoggerFactory.getLogger(TrackingAspect.class);
    private static final Map<String, String> CONTROLLER_API_MAP = Map.of(
            "HelloWorldController", "helloworld",
            "HashController", "hash",
            "BubbleSortController", "bubblesort"
    );
    private final TrackingRecordRepository trackingRepo;
    private final ObjectMapper objectMapper;
    public TrackingAspect(TrackingRecordRepository trackingRepo, ObjectMapper objectMapper) {
        this.trackingRepo = trackingRepo;
        this.objectMapper = objectMapper;
    }
    @Around("execution(* com.example.demo.controller.HelloWorldController.*(..)) || " +
            "execution(* com.example.demo.controller.HashController.*(..)) || " +
            "execution(* com.example.demo.controller.BubbleSortController.*(..))")
    public Object recordTracking(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Long userId) {
                String controllerName = joinPoint.getTarget().getClass().getSimpleName();
                String apiName = CONTROLLER_API_MAP.getOrDefault(controllerName, joinPoint.getSignature().getName());
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                String[] paramNames = signature.getParameterNames();
                Object[] args = joinPoint.getArgs();
                String paramsJson = "{}";
                try {
                    if (paramNames != null && args != null) {
                        java.util.Map<String, Object> paramsMap = new java.util.HashMap<>();
                        for (int i = 0; i < Math.min(paramNames.length, args.length); i++) {
                            if (args[i] != null && !(args[i] instanceof jakarta.servlet.http.HttpServletRequest)
                                    && !(args[i] instanceof jakarta.servlet.http.HttpServletResponse)) {
                                paramsMap.put(paramNames[i], args[i]);
                            }
                        }
                        paramsJson = objectMapper.writeValueAsString(paramsMap);
                    }
                } catch (Exception e) { log.warn("Failed to serialize params", e); }
                String ipAddress = "unknown";
                try {
                    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attrs != null) {
                        HttpServletRequest request = attrs.getRequest();
                        ipAddress = request.getRemoteAddr();
                    }
                } catch (Exception e) { log.warn("Failed to get IP", e); }
                TrackingRecord record = new TrackingRecord(userId, apiName, paramsJson, ipAddress);
                record.setCallTime(LocalDateTime.now(ZoneOffset.UTC));
                trackingRepo.save(record);
            }
        } catch (Exception e) {
            log.error("Tracking aspect error", e);
        }
        return joinPoint.proceed();
    }
}
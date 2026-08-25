package com.example.demo.interceptor;

import com.example.demo.model.CallRecord;
import com.example.demo.model.UserInfo;
import com.example.demo.repository.CallRecordRepository;
import com.example.demo.service.PersonService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Component
public class CallTrackingInterceptor implements HandlerInterceptor {

    @Autowired
    private CallRecordRepository callRecordRepository;

    @Autowired
    private PersonService personService;

    private final ThreadLocal<Long> startTimeLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String path = request.getRequestURI();
        // 只跟踪 /api/ 接口，排除 /api/auth/ 和 /api/export, /api/stats
        if (!path.startsWith("/api/") || path.equals("/api/auth/login")
                || path.startsWith("/api/export") || path.startsWith("/api/stats")) {
            return true;
        }

        startTimeLocal.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        // Not used
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        Long startTime = startTimeLocal.get();
        if (startTime == null) {
            return;
        }
        startTimeLocal.remove();

        String path = request.getRequestURI();
        if (path.equals("/api/auth/login") || path.startsWith("/api/export")
                || path.startsWith("/api/stats")) {
            return;
        }

        String callerId = (String) request.getAttribute("callerId");
        if (callerId == null) {
            return;
        }

        long responseTime = System.currentTimeMillis() - startTime;

        // 通过 PersonService 获取人员维度信息（模拟对接外部系统）
        UserInfo personInfo = personService.getPersonInfo(callerId);
        String callerType = personInfo != null ? personInfo.getCallerType() : "未知";
        String callerLevel = personInfo != null ? personInfo.getCallerLevel() : "未知";
        String callerDept = personInfo != null ? personInfo.getCallerDept() : "未知";

        // 提取接口名
        String apiName = path;
        if (path.startsWith("/api/")) {
            apiName = path.substring(5); // 去掉 "/api/"
        }

        CallRecord record = new CallRecord(callerId, callerType, callerLevel,
                callerDept, apiName, LocalDateTime.now(), responseTime);
        callRecordRepository.save(record);
    }
}
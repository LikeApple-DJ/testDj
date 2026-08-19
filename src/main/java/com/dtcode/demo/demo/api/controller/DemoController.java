package com.dtcode.demo.demo.api.controller;

import com.dtcode.demo.common.model.ApiResponse;
import com.dtcode.demo.demo.model.dto.BubbleSortDTO;
import com.dtcode.demo.demo.model.dto.BubbleSortRequest;
import com.dtcode.demo.demo.model.dto.HashDTO;
import com.dtcode.demo.demo.model.dto.HashRequest;
import com.dtcode.demo.demo.model.dto.HelloWorldDTO;
import com.dtcode.demo.demo.model.dto.HelloWorldRequest;
import com.dtcode.demo.demo.service.DemoService;
import com.dtcode.demo.analytics.service.AnalyticsService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示接口控制器
 *
 * @author DTCoder
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final DemoService demoService;
    private final AnalyticsService analyticsService;

    public DemoController(DemoService demoService, AnalyticsService analyticsService) {
        this.demoService = demoService;
        this.analyticsService = analyticsService;
    }

    @PostMapping("/helloworld")
    public ApiResponse<HelloWorldDTO> helloWorld(
            @RequestBody(required = false) HelloWorldRequest request,
            HttpServletRequest httpRequest) {
        String name = (request != null) ? request.getName() : null;
        String callerId = resolveCallerId(httpRequest);
        String callerName = resolveCallerName(httpRequest);
        String callerType = resolveCallerType(httpRequest);
        String callerLevel = resolveCallerLevel(httpRequest);
        String callerDept = resolveCallerDept(httpRequest);

        demoService.setCallerContext(callerId);
        try {
            long startTime = System.currentTimeMillis();
            HelloWorldDTO result = demoService.helloWorld(name);
            long duration = System.currentTimeMillis() - startTime;
            analyticsService.recordCallAsync("helloworld", name, duration,
                    callerId, callerName, callerType, callerLevel, callerDept);
            return ApiResponse.success(result);
        } finally {
            demoService.clearCallerContext();
        }
    }

    @PostMapping("/hash")
    public ApiResponse<HashDTO> hash(
            @RequestBody HashRequest request,
            HttpServletRequest httpRequest) {
        String input = (request != null) ? request.getInput() : null;
        String callerId = resolveCallerId(httpRequest);
        String callerName = resolveCallerName(httpRequest);
        String callerType = resolveCallerType(httpRequest);
        String callerLevel = resolveCallerLevel(httpRequest);
        String callerDept = resolveCallerDept(httpRequest);

        demoService.setCallerContext(callerId);
        try {
            long startTime = System.currentTimeMillis();
            HashDTO result = demoService.hash(input);
            long duration = System.currentTimeMillis() - startTime;
            analyticsService.recordCallAsync("hash", input, duration,
                    callerId, callerName, callerType, callerLevel, callerDept);
            return ApiResponse.success(result);
        } finally {
            demoService.clearCallerContext();
        }
    }

    @PostMapping("/bubble-sort")
    public ApiResponse<BubbleSortDTO> bubbleSort(
            @RequestBody BubbleSortRequest request,
            HttpServletRequest httpRequest) {
        String callerId = resolveCallerId(httpRequest);
        String callerName = resolveCallerName(httpRequest);
        String callerType = resolveCallerType(httpRequest);
        String callerLevel = resolveCallerLevel(httpRequest);
        String callerDept = resolveCallerDept(httpRequest);

        demoService.setCallerContext(callerId);
        try {
            long startTime = System.currentTimeMillis();
            BubbleSortDTO result = demoService.bubbleSort(
                    request != null ? request.getNumbers() : null);
            long duration = System.currentTimeMillis() - startTime;
            String params = (request != null && request.getNumbers() != null)
                    ? "size=" + request.getNumbers().size() : "null";
            analyticsService.recordCallAsync("bubble-sort", params, duration,
                    callerId, callerName, callerType, callerLevel, callerDept);
            return ApiResponse.success(result);
        } finally {
            demoService.clearCallerContext();
        }
    }

    /**
     * 从请求头解析调用人ID
     */
    private String resolveCallerId(HttpServletRequest request) {
        String callerId = request.getHeader("X-Caller-Id");
        return (callerId != null && !callerId.trim().isEmpty()) ? callerId.trim() : "UNKNOWN";
    }

    /**
     * 从请求头解析调用人姓名
     */
    private String resolveCallerName(HttpServletRequest request) {
        String callerName = request.getHeader("X-Caller-Name");
        return (callerName != null && !callerName.trim().isEmpty()) ? callerName.trim() : "";
    }

    /**
     * 从请求头解析调用人类型
     */
    private String resolveCallerType(HttpServletRequest request) {
        String callerType = request.getHeader("X-Caller-Type");
        return (callerType != null && !callerType.trim().isEmpty()) ? callerType.trim() : "";
    }

    /**
     * 从请求头解析调用人层级
     */
    private String resolveCallerLevel(HttpServletRequest request) {
        String callerLevel = request.getHeader("X-Caller-Level");
        return (callerLevel != null && !callerLevel.trim().isEmpty()) ? callerLevel.trim() : "";
    }

    /**
     * 从请求头解析调用人部门
     */
    private String resolveCallerDept(HttpServletRequest request) {
        String callerDept = request.getHeader("X-Caller-Dept");
        return (callerDept != null && !callerDept.trim().isEmpty()) ? callerDept.trim() : "";
    }
}

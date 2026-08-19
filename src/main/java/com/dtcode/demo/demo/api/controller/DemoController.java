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
    public ApiResponse<HelloWorldDTO> helloWorld(@RequestBody(required = false) HelloWorldRequest request) {
        String name = (request != null) ? request.getName() : null;
        long startTime = System.currentTimeMillis();
        HelloWorldDTO result = demoService.helloWorld(name);
        long duration = System.currentTimeMillis() - startTime;
        analyticsService.recordCallAsync("helloworld", name, duration);
        return ApiResponse.success(result);
    }

    @PostMapping("/hash")
    public ApiResponse<HashDTO> hash(@RequestBody HashRequest request) {
        String input = (request != null) ? request.getInput() : null;
        long startTime = System.currentTimeMillis();
        HashDTO result = demoService.hash(input);
        long duration = System.currentTimeMillis() - startTime;
        analyticsService.recordCallAsync("hash", input, duration);
        return ApiResponse.success(result);
    }

    @PostMapping("/bubble-sort")
    public ApiResponse<BubbleSortDTO> bubbleSort(@RequestBody BubbleSortRequest request) {
        long startTime = System.currentTimeMillis();
        BubbleSortDTO result = demoService.bubbleSort(
                request != null ? request.getNumbers() : null);
        long duration = System.currentTimeMillis() - startTime;
        String params = (request != null && request.getNumbers() != null)
                ? "size=" + request.getNumbers().size() : "null";
        analyticsService.recordCallAsync("bubble-sort", params, duration);
        return ApiResponse.success(result);
    }
}

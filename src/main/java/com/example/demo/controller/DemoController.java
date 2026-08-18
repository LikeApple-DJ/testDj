package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.exception.DemoException;
import com.example.demo.model.request.ExportRequest;
import com.example.demo.model.request.HashRequest;
import com.example.demo.model.request.SortRequest;
import com.example.demo.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Demo 演示模块控制器。
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    /**
     * HelloWorld 接口。
     *
     * @return 问候语
     */
    @GetMapping("/hello")
    public ApiResponse<Map<String, String>> hello() {
        long start = System.currentTimeMillis();
        try {
            String message = demoService.hello();
            Map<String, String> data = new HashMap<>();
            data.put("message", message);
            LOGGER.info("GET /api/demo/hello success, cost={}ms", System.currentTimeMillis() - start);
            return ApiResponse.success(data);
        } catch (DemoException e) {
            LOGGER.error("GET /api/demo/hello error: {}", e.getMessage());
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            LOGGER.error("GET /api/demo/hello unexpected error", e);
            return ApiResponse.error("DEMO_005", "系统内部错误");
        }
    }

    /**
     * 哈希计算接口。
     *
     * @param request 哈希请求参数
     * @return 哈希结果
     */
    @PostMapping("/hash")
    public ApiResponse<Map<String, Object>> hash(@RequestBody HashRequest request) {
        long start = System.currentTimeMillis();
        try {
            String algorithm = request.getAlgorithm();
            if (algorithm == null || algorithm.isEmpty()) {
                algorithm = "SHA-256";
            }
            String hashResult = demoService.hash(request.getInput(), algorithm);

            Map<String, Object> data = new HashMap<>();
            data.put("input", request.getInput());
            data.put("algorithm", algorithm);
            data.put("hash", hashResult);

            LOGGER.info("POST /api/demo/hash success, cost={}ms", System.currentTimeMillis() - start);
            return ApiResponse.success(data);
        } catch (DemoException e) {
            LOGGER.error("POST /api/demo/hash error: {}", e.getMessage());
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            LOGGER.error("POST /api/demo/hash unexpected error", e);
            return ApiResponse.error("DEMO_005", "系统内部错误");
        }
    }

    /**
     * 冒泡排序接口。
     *
     * @param request 排序请求参数
     * @return 排序结果
     */
    @PostMapping("/bubble-sort")
    public ApiResponse<Map<String, Object>> bubbleSort(@RequestBody SortRequest request) {
        long start = System.currentTimeMillis();
        try {
            int[] original = request.getArray();
            int[] sorted = demoService.bubbleSort(original);

            // 计算交换步数
            int steps = countBubbleSortSteps(original);

            Map<String, Object> data = new HashMap<>();
            data.put("original", original);
            data.put("sorted", sorted);
            data.put("steps", steps);

            LOGGER.info("POST /api/demo/bubble-sort success, cost={}ms", System.currentTimeMillis() - start);
            return ApiResponse.success(data);
        } catch (DemoException e) {
            LOGGER.error("POST /api/demo/bubble-sort error: {}", e.getMessage());
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            LOGGER.error("POST /api/demo/bubble-sort unexpected error", e);
            return ApiResponse.error("DEMO_005", "系统内部错误");
        }
    }

    /**
     * 结果导出接口。
     *
     * @param request 导出请求参数
     * @return 文件下载响应
     */
    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody ExportRequest request) {
        long start = System.currentTimeMillis();
        try {
            byte[] content = demoService.export(request.getType(), request.getFormat(), request.getData());

            String resolvedFormat = request.getFormat() == null ? "json" : request.getFormat().toLowerCase();
            MediaType mediaType = "csv".equals(resolvedFormat)
                    ? MediaType.parseMediaType("text/csv")
                    : MediaType.APPLICATION_JSON;

            String filename = "export_" + request.getType() + "." + ("csv".equals(resolvedFormat) ? "csv" : "json");

            LOGGER.info("POST /api/demo/export success, cost={}ms", System.currentTimeMillis() - start);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .contentType(mediaType)
                    .body(content);
        } catch (DemoException e) {
            LOGGER.error("POST /api/demo/export error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(("{\"code\":\"" + e.getErrorCode() + "\",\"msg\":\"" + e.getMessage() + "\"}").getBytes());
        } catch (Exception e) {
            LOGGER.error("POST /api/demo/export unexpected error", e);
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"code\":\"DEMO_005\",\"msg\":\"系统内部错误\"}".getBytes());
        }
    }

    private int countBubbleSortSteps(int[] array) {
        int[] copy = java.util.Arrays.copyOf(array, array.length);
        int steps = 0;
        int n = copy.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (copy[j] > copy[j + 1]) {
                    int temp = copy[j];
                    copy[j] = copy[j + 1];
                    copy[j + 1] = temp;
                    steps++;
                }
            }
        }
        return steps;
    }
}
package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import com.example.demo.dto.MetricsResponse;
import com.example.demo.service.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * 埋点报表查询接口控制器。
 */
@RestController
@RequestMapping("/api")
public class MetricsController {

    private static final Logger log = LoggerFactory.getLogger(MetricsController.class);

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    /**
     * 按维度查询埋点调用统计报表。
     */
    @GetMapping("/metrics")
    public ResponseEntity<ApiResult<MetricsResponse>> metrics(
            @RequestParam String dimension,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
            MetricsResponse data = metricsService.queryByDimension(dimension, start, end);
            return ResponseEntity.ok(ApiResult.success(data));
        } catch (DateTimeParseException e) {
            log.error("日期格式非法: startDate={}, endDate={}", startDate, endDate, e);
            return ResponseEntity.badRequest()
                    .body(ApiResult.error(400, "日期格式错误，请使用 yyyy-MM-dd 格式"));
        } catch (IllegalArgumentException e) {
            log.error("指标查询参数非法: dimension={}", dimension, e);
            return ResponseEntity.badRequest()
                    .body(ApiResult.error(400, e.getMessage()));
        }
    }
}
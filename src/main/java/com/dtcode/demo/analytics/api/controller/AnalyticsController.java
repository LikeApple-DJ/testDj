package com.dtcode.demo.analytics.api.controller;

import com.dtcode.demo.analytics.model.dto.CallSummaryDTO;
import com.dtcode.demo.analytics.model.dto.DistributionDTO;
import com.dtcode.demo.analytics.model.dto.TrendDTO;
import com.dtcode.demo.analytics.service.impl.AnalyticsServiceImpl;
import com.dtcode.demo.common.model.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调用分析控制器
 *
 * @author DTCoder
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsServiceImpl analyticsServiceImpl;

    public AnalyticsController(AnalyticsServiceImpl analyticsServiceImpl) {
        this.analyticsServiceImpl = analyticsServiceImpl;
    }

    /**
     * 调用统计汇总查询
     */
    @GetMapping("/summary")
    public ApiResponse<CallSummaryDTO> getSummary(
            @RequestParam String dimension,
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        CallSummaryDTO result = analyticsServiceImpl.getSummary(dimension, apiName, startDate, endDate);
        return ApiResponse.success(result);
    }

    /**
     * 调用趋势查询
     */
    @GetMapping("/trend")
    public ApiResponse<TrendDTO> getTrend(
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false) String granularity,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        TrendDTO result = analyticsServiceImpl.getTrend(apiName, granularity, startDate, endDate);
        return ApiResponse.success(result);
    }

    /**
     * 调用分布查询
     */
    @GetMapping("/distribution")
    public ApiResponse<DistributionDTO> getDistribution(
            @RequestParam String dimension,
            @RequestParam(required = false) String apiName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        DistributionDTO result = analyticsServiceImpl.getDistribution(dimension, apiName, startDate, endDate);
        return ApiResponse.success(result);
    }
}

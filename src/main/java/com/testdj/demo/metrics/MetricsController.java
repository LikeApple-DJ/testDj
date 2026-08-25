package com.testdj.demo.metrics;

import com.testdj.demo.common.ApiResponse;
import com.testdj.demo.common.ErrorCode;
import com.testdj.demo.exception.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/demo/metrics")
public class MetricsController {

    private final MetricService metricService;

    public MetricsController(MetricService metricService) {
        this.metricService = metricService;
    }

    @GetMapping("/report")
    public ApiResponse<List<ReportItem>> report(
            @RequestParam("dimension") Dimension dimension,
            @RequestParam("startDate") Instant startDate,
            @RequestParam("endDate") Instant endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException(ErrorCode.METRICS_INVALID_DATE_RANGE,
                    ErrorCode.METRICS_INVALID_DATE_RANGE_MSG);
        }
        return ApiResponse.ok(metricService.report(dimension, startDate, endDate));
    }
}

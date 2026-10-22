package com.dtazziboot.tools.controller;

import com.dtazziboot.tools.model.dto.DistributionItem;
import com.dtazziboot.tools.model.dto.DistributionResult;
import com.dtazziboot.tools.model.dto.ReportQuery;
import com.dtazziboot.tools.model.dto.TrendPoint;
import com.dtazziboot.tools.model.dto.TrendResult;
import com.dtazziboot.tools.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调用情况报表接口（W05/W06）。
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/trend")
    public TrendResult trend(@RequestParam(required = false) String startTime,
                             @RequestParam(required = false) String endTime,
                             @RequestParam(required = false) String apiCode) {
        ReportQuery query = new ReportQuery();
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        query.setApiCode(apiCode);
        TrendResult result = new TrendResult();
        result.setPoints(reportService.trend(query));
        return result;
    }

    @GetMapping("/distribution")
    public DistributionResult distribution(@RequestParam(required = false) String dimension,
                                           @RequestParam(required = false) String startTime,
                                           @RequestParam(required = false) String endTime,
                                           @RequestParam(required = false) String apiCode) {
        ReportQuery query = new ReportQuery();
        query.setDimension(dimension);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        query.setApiCode(apiCode);
        DistributionResult result = new DistributionResult();
        result.setItems(reportService.distribution(query));
        return result;
    }
}
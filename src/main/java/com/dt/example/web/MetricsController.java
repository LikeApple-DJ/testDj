package com.dt.example.web;

import com.dt.example.common.Result;
import com.dt.example.metrics.MetricsQueryResponse;
import com.dt.example.metrics.MetricsService;
import com.dt.example.metrics.Person;
import com.dt.example.metrics.PersonDictionary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 埋点报表与人员字典接口。
 */
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;
    private final PersonDictionary dictionary;

    public MetricsController(MetricsService metricsService, PersonDictionary dictionary) {
        this.metricsService = metricsService;
        this.dictionary = dictionary;
    }

    @GetMapping("/query")
    public Result<MetricsQueryResponse> query(
        @RequestParam(defaultValue = "personType") String dimension,
        @RequestParam(defaultValue = "pie") String chartType) {
        return Result.ok(metricsService.query(dimension, chartType));
    }

    @GetMapping("/persons")
    public Result<List<Person>> persons() {
        return Result.ok(dictionary.all());
    }
}
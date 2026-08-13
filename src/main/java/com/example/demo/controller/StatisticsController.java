package com.example.demo.controller;

import com.example.demo.dto.StatisticsResponse;
import com.example.demo.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public StatisticsResponse statistics(
            @RequestParam(defaultValue = "userDept") String dimension,
            @RequestParam(defaultValue = "all") String period) {
        return statisticsService.getStatistics(dimension, period);
    }
}

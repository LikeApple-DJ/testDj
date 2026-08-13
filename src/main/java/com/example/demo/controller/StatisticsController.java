package com.example.demo.controller;

import com.example.demo.dto.StatisticsResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/demo")
public class StatisticsController {

    private static final Set<String> VALID_DIMENSIONS = Set.of("userType", "userLevel", "userDept");

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public StatisticsResponse statistics(
            @RequestParam(defaultValue = "userDept") String dimension,
            @RequestParam(defaultValue = "all") String period) {

        if (!VALID_DIMENSIONS.contains(dimension)) {
            throw new BusinessException("STAT_001", "dimension 参数不合法，必须为 userType/userLevel/userDept");
        }

        return statisticsService.getStatistics(dimension, period);
    }
}

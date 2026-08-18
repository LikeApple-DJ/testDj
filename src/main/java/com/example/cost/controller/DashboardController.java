package com.example.cost.controller;

import com.example.cost.common.Result;
import com.example.cost.dto.DashboardVO;
import com.example.cost.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cost")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard(
            @RequestParam(defaultValue = "month") String periodType,
            @RequestParam String periodValue) {
        return Result.success(dashboardService.getDashboard(periodType, periodValue));
    }
}
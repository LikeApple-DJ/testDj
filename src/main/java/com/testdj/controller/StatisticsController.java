package com.testdj.controller;

import com.testdj.dto.StatisticsResponse;
import com.testdj.dto.TrackRequest;
import com.testdj.service.StatisticsService;
import com.testdj.service.TrackingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatisticsController {

    private final TrackingService trackingService;
    private final StatisticsService statisticsService;

    public StatisticsController(TrackingService trackingService, StatisticsService statisticsService) {
        this.trackingService = trackingService;
        this.statisticsService = statisticsService;
    }

    @PostMapping("/track")
    public ResponseEntity<Map<String, Object>> track(@Valid @RequestBody TrackRequest request) {
        trackingService.track(
                request.getApiName(),
                request.getCaller(),
                request.getDepartment(),
                request.getLevel(),
                request.getType()
        );
        return ResponseEntity.ok(Map.of("code", 0, "message", "success"));
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> statistics(@RequestParam("dimension") String dimension) {
        try {
            StatisticsResponse response = statisticsService.getStatistics(dimension);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 1, "message", e.getMessage()));
        }
    }

    @GetMapping("/statistics/trend")
    public ResponseEntity<StatisticsResponse> trend() {
        return ResponseEntity.ok(statisticsService.getTrend());
    }
}
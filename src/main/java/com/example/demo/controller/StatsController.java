package com.example.demo.controller;

import com.example.demo.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestParam(defaultValue = "type") String dimension,
            @RequestParam(defaultValue = "bar") String chart) {
        return ResponseEntity.ok(statsService.getStats(dimension));
    }
}
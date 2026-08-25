package com.example.demo.controller;

import com.example.demo.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/calls")
    public ResponseEntity<?> getCalls(
            @RequestParam(defaultValue = "type") String dimension,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        try {
            Map<String, Object> result = statsService.getStatsByDimension(dimension, start, end);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage(), "status", 400));
        }
    }

    @GetMapping("/callers")
    public ResponseEntity<?> getCallers() {
        return ResponseEntity.ok(statsService.getCallers());
    }
}
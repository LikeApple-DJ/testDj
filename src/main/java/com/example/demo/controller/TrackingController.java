package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(
            @RequestParam(value = "dimension", defaultValue = "callerType") String dimension,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {

        Map<String, Object> stats = trackingService.getStats(dimension, startDate, endDate);
        return Result.success(stats);
    }

    @GetMapping("/records")
    public Result<Map<String, Object>> records(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Map<String, Object> records = trackingService.getRecords(page, size);
        return Result.success(records);
    }
}
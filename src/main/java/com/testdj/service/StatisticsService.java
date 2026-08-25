package com.testdj.service;

import com.testdj.dto.StatisticsResponse;
import com.testdj.repository.ApiCallLogRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final ApiCallLogRepository repository;

    public StatisticsService(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Aggregate statistics by dimension: department, level, or type.
     */
    public StatisticsResponse getStatistics(String dimension) {
        List<Object[]> raw;
        switch (dimension.toLowerCase()) {
            case "department":
                raw = repository.countByDepartment();
                break;
            case "level":
                raw = repository.countByLevel();
                break;
            case "type":
                raw = repository.countByType();
                break;
            default:
                throw new IllegalArgumentException("Unsupported dimension: " + dimension + ". Supported: department, level, type");
        }

        List<Map<String, Object>> data = raw.stream().map(row -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("dimension", dimension);
            map.put("value", row[0]);
            map.put("count", row[1]);
            return map;
        }).collect(Collectors.toList());

        return new StatisticsResponse(dimension, data);
    }

    /**
     * Trend statistics by date.
     */
    public StatisticsResponse getTrend() {
        List<Object[]> raw = repository.countByDate();
        List<Map<String, Object>> data = raw.stream().map(row -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("date", row[0] != null ? row[0].toString() : null);
            map.put("count", row[1]);
            return map;
        }).collect(Collectors.toList());

        return new StatisticsResponse("trend", data);
    }
}
package com.example.demo.service;

import com.example.demo.repository.InvocationLogRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final InvocationLogRepository repository;

    public StatsService(InvocationLogRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getStats(String dimension) {
        List<Object[]> raw;
        switch (dimension.toLowerCase()) {
            case "type"       -> raw = repository.countGroupByUserType();
            case "level"      -> raw = repository.countGroupByUserLevel();
            case "department" -> raw = repository.countGroupByUserDepartment();
            case "api"        -> raw = repository.countGroupByApi();
            default -> throw new IllegalArgumentException(
                    "Unknown dimension: " + dimension + ". Allowed: type, level, department, api");
        }

        List<Map<String, Object>> data = raw.stream()
                .map(row -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("key", row[0]);
                    item.put("count", row[1]);
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dimension", dimension);
        result.put("data", data);
        return result;
    }
}
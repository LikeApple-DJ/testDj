package com.example.demo.service;

import com.example.demo.dto.StatisticsResponse;
import com.example.demo.dto.StatisticsResponse.DimensionItem;
import com.example.demo.repository.ApiCallLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService {

    private final ApiCallLogRepository repository;

    public StatisticsService(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    public StatisticsResponse getStatistics(String dimension, String period) {
        LocalDateTime since = calculateSince(period);
        List<Object[]> rawResults;

        switch (dimension) {
            case "userType":
                rawResults = (since != null) ? repository.countByUserTypeSince(since) : repository.countByUserType();
                break;
            case "userLevel":
                rawResults = (since != null) ? repository.countByUserLevelSince(since) : repository.countByUserLevel();
                break;
            case "userDept":
                rawResults = (since != null) ? repository.countByUserDeptSince(since) : repository.countByUserDept();
                break;
            default:
                throw new IllegalArgumentException("Unsupported dimension: " + dimension);
        }

        List<DimensionItem> data = new ArrayList<>();
        int total = 0;
        for (Object[] row : rawResults) {
            String label = row[0] != null ? (String) row[0] : "未知";
            int count = ((Long) row[1]).intValue();
            data.add(new DimensionItem(label, count));
            total += count;
        }

        return new StatisticsResponse(dimension, data, total);
    }

    private LocalDateTime calculateSince(String period) {
        if (period == null || "all".equalsIgnoreCase(period)) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        return switch (period.toLowerCase()) {
            case "7d" -> now.minusDays(7);
            case "30d" -> now.minusDays(30);
            default -> null;
        };
    }
}

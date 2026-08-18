package com.example.demo.service;

import com.example.demo.dto.MetricsItem;
import com.example.demo.dto.MetricsResponse;
import com.example.demo.repository.MetricsRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private final MetricsRecordRepository repository;

    public MetricsService(MetricsRecordRepository repository) {
        this.repository = repository;
    }

    public MetricsResponse queryByDimension(String dimension, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        List<Object[]> raw;
        String dimLabel;

        switch (dimension) {
            case "personType":
                raw = repository.countByCallerType(start, end);
                dimLabel = "personType";
                break;
            case "level":
                raw = repository.countByCallerLevel(start, end);
                dimLabel = "level";
                break;
            case "department":
                raw = repository.countByCallerDept(start, end);
                dimLabel = "department";
                break;
            default:
                throw new IllegalArgumentException("不支持的维度: " + dimension
                        + "，仅支持 personType / level / department");
        }

        Map<String, MetricsItem> itemMap = new LinkedHashMap<>();
        int totalCalls = 0;

        for (Object[] row : raw) {
            String dim = (String) row[0];
            String apiPath = (String) row[1];
            int count = ((Number) row[2]).intValue();
            totalCalls += count;

            itemMap.computeIfAbsent(dim, k -> new MetricsItem(k, 0));
            MetricsItem item = itemMap.get(dim);
            item.setCount(item.getCount() + count);

            if (item.getSubItems() == null) {
                item.setSubItems(new ArrayList<>());
            }
            item.getSubItems().add(new MetricsItem(apiPath, count));
        }

        List<MetricsItem> items = new ArrayList<>(itemMap.values());
        return new MetricsResponse(dimLabel, items, totalCalls);
    }
}
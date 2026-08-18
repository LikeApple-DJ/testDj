package com.example.demo.service;

import com.example.demo.dto.MetricsItem;
import com.example.demo.dto.MetricsResponse;
import com.example.demo.repository.MetricsRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 埋点报表查询服务。
 */
@Service
public class MetricsService {

    private final MetricsRecordRepository repository;

    public MetricsService(MetricsRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * 按指定维度聚合查询埋点调用统计。
     *
     * @param dimension 聚合维度：personType / level / department
     * @param startDate 查询起始日期（含），null 表示不限
     * @param endDate   查询截止日期（含），null 表示不限
     * @return 聚合后的报表数据
     * @throws IllegalArgumentException 如果 dimension 不支持
     */
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
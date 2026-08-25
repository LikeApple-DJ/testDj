package com.example.demo.service;

import com.example.demo.repository.CallRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class StatsService {

    @Autowired
    private CallRecordRepository callRecordRepository;

    public Map<String, Object> getStatsByDimension(String dimension, String start, String end) {
        LocalDateTime startDate = start != null && !start.isBlank() ? LocalDateTime.parse(start) : null;
        LocalDateTime endDate = end != null && !end.isBlank() ? LocalDateTime.parse(end) : null;

        List<Object[]> rawData;
        String dim;

        switch (dimension.toLowerCase()) {
            case "type":
                rawData = callRecordRepository.countByCallerType(startDate, endDate);
                dim = "type";
                break;
            case "level":
                rawData = callRecordRepository.countByCallerLevel(startDate, endDate);
                dim = "level";
                break;
            case "dept":
                rawData = callRecordRepository.countByCallerDept(startDate, endDate);
                dim = "dept";
                break;
            case "time":
                rawData = callRecordRepository.countByTimeTrend(startDate, endDate);
                dim = "time";
                break;
            default:
                throw new IllegalArgumentException("Unsupported dimension: " + dimension);
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Object[] row : rawData) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", row[0]);
            item.put("value", row[1]);
            dataList.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dimension", dim);
        result.put("data", dataList);
        return result;
    }

    public Map<String, Object> getCallers() {
        List<Object[]> raw = callRecordRepository.findDistinctCallers();
        List<Map<String, String>> callers = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, String> c = new HashMap<>();
            c.put("callerId", (String) row[0]);
            c.put("callerType", (String) row[1]);
            c.put("callerLevel", (String) row[2]);
            c.put("callerDept", (String) row[3]);
            callers.add(c);
        }
        return Map.of("callers", callers);
    }
}
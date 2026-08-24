package com.example.demo.service;

import com.example.demo.entity.TrackingRecord;
import com.example.demo.repository.TrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class TrackingService {

    @Autowired
    private TrackingRepository trackingRepository;

    public void record(String apiName, String callerName, String callerType,
                       String callerLevel, String callerDept, String extraInfo) {
        TrackingRecord record = new TrackingRecord(
                apiName,
                callerName,
                callerType,
                callerLevel,
                callerDept,
                extraInfo,
                LocalDateTime.now()
        );
        trackingRepository.save(record);
    }

    public Map<String, Object> getStats(String dimension, String startDate, String endDate) {
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        if (dimension == null || dimension.isBlank()) {
            dimension = "callerType";
        }

        switch (dimension.toLowerCase()) {
            case "callertype":
                for (Object[] row : trackingRepository.countGroupByCallerType()) {
                    labels.add((String) row[0]);
                    values.add((Long) row[1]);
                }
                break;
            case "callerlevel":
                for (Object[] row : trackingRepository.countGroupByCallerLevel()) {
                    labels.add((String) row[0]);
                    values.add((Long) row[1]);
                }
                break;
            case "callerdept":
                for (Object[] row : trackingRepository.countGroupByCallerDept()) {
                    labels.add((String) row[0]);
                    values.add((Long) row[1]);
                }
                break;
            case "time":
                LocalDateTime start = LocalDateTime.now().minusDays(7);
                LocalDateTime end = LocalDateTime.now();
                if (startDate != null && !startDate.isBlank()) {
                    try {
                        start = LocalDateTime.parse(startDate + " 00:00:00",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    } catch (DateTimeParseException ignored) {}
                }
                if (endDate != null && !endDate.isBlank()) {
                    try {
                        end = LocalDateTime.parse(endDate + " 23:59:59",
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    } catch (DateTimeParseException ignored) {}
                }
                for (Object[] row : trackingRepository.countByTimeRange(start, end)) {
                    labels.add((String) row[0]);
                    values.add((Long) row[1]);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported dimension: " + dimension);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("labels", labels);
        result.put("values", values);
        return result;
    }

    public Map<String, Object> getRecords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackingRecord> recordPage = trackingRepository.findAll(pageable);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> records = new ArrayList<>();
        for (TrackingRecord record : recordPage.getContent()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", record.getId());
            item.put("apiName", record.getApiName());
            item.put("callerName", record.getCallerName());
            item.put("callerType", record.getCallerType());
            item.put("callerLevel", record.getCallerLevel());
            item.put("callerDept", record.getCallerDept());
            item.put("extraInfo", record.getExtraInfo());
            item.put("callTime", record.getCallTime() != null ? record.getCallTime().format(formatter) : null);
            records.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", records);
        result.put("page", recordPage.getNumber());
        result.put("size", recordPage.getSize());
        result.put("totalElements", recordPage.getTotalElements());
        result.put("totalPages", recordPage.getTotalPages());
        return result;
    }

    public List<TrackingRecord> getRecordsByApiName(String apiName) {
        return trackingRepository.findByApiName(apiName);
    }

    public List<TrackingRecord> getRecordsByApiNameAndCallerName(String apiName, String callerName) {
        return trackingRepository.findByApiNameAndCallerName(apiName, callerName);
    }
}
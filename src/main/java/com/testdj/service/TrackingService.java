package com.testdj.service;

import com.testdj.entity.ApiCallLog;
import com.testdj.repository.ApiCallLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TrackingService {

    private final ApiCallLogRepository repository;

    public TrackingService(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Record an API call log entry.
     */
    public ApiCallLog track(String apiName, String caller, String department, String level, String type) {
        ApiCallLog log = new ApiCallLog(apiName, caller, department, level, type, LocalDateTime.now());
        return repository.save(log);
    }
}
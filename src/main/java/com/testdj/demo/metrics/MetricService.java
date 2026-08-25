package com.testdj.demo.metrics;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MetricService {

    private final MetricRepository metricRepository;

    public MetricService(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    public MetricEvent track(MetricEvent event) {
        return metricRepository.save(event);
    }

    public List<ReportItem> report(Dimension dimension, Instant start, Instant end) {
        return switch (dimension) {
            case USER_TYPE -> metricRepository.reportByUserType(start, end);
            case USER_LEVEL -> metricRepository.reportByUserLevel(start, end);
            case USER_DEPT -> metricRepository.reportByUserDept(start, end);
        };
    }
}
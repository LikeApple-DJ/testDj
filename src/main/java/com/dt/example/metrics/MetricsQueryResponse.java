package com.dt.example.metrics;

import java.util.List;

public record MetricsQueryResponse(
    String dimension,
    String chartType,
    List<String> categories,
    List<ChartSeries> series,
    long total) {
}
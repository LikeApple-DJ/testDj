package com.dt.example.metrics;

import java.util.List;

public record ChartSeries(String name, List<Double> values) {
}
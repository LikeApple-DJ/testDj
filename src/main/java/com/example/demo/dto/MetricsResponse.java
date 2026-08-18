package com.example.demo.dto;

import java.util.List;

public class MetricsResponse {
    private String dimension;
    private List<MetricsItem> items;
    private int totalCalls;

    public MetricsResponse(String dimension, List<MetricsItem> items, int totalCalls) {
        this.dimension = dimension;
        this.items = items;
        this.totalCalls = totalCalls;
    }

    public String getDimension() { return dimension; }
    public List<MetricsItem> getItems() { return items; }
    public int getTotalCalls() { return totalCalls; }
}
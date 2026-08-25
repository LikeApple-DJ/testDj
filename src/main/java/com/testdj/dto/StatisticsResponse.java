package com.testdj.dto;

import java.util.List;
import java.util.Map;

public class StatisticsResponse {
    private String dimension;
    private List<Map<String, Object>> data;

    public StatisticsResponse() {}

    public StatisticsResponse(String dimension, List<Map<String, Object>> data) {
        this.dimension = dimension;
        this.data = data;
    }

    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }

    public List<Map<String, Object>> getData() { return data; }
    public void setData(List<Map<String, Object>> data) { this.data = data; }
}
package com.example.demo.dto;

import java.util.List;

public class StatisticsResponse {
    private String dimension;
    private List<DimensionItem> data;
    private int total;

    public StatisticsResponse() {}

    public StatisticsResponse(String dimension, List<DimensionItem> data, int total) {
        this.dimension = dimension;
        this.data = data;
        this.total = total;
    }

    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public List<DimensionItem> getData() { return data; }
    public void setData(List<DimensionItem> data) { this.data = data; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public static class DimensionItem {
        private String label;
        private int count;

        public DimensionItem() {}

        public DimensionItem(String label, int count) {
            this.label = label;
            this.count = count;
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }
}

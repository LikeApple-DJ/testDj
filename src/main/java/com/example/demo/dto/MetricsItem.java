package com.example.demo.dto;

import java.util.List;

public class MetricsItem {
    private String label;
    private int count;
    private List<MetricsItem> subItems;

    public MetricsItem(String label, int count) {
        this.label = label;
        this.count = count;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public List<MetricsItem> getSubItems() { return subItems; }
    public void setSubItems(List<MetricsItem> subItems) { this.subItems = subItems; }
}
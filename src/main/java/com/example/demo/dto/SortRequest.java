package com.example.demo.dto;

public class SortRequest {
    private Integer arraySize; // 默认 10
    private Integer min;       // 默认 1
    private Integer max;       // 默认 100

    public Integer getArraySize() { return arraySize; }
    public void setArraySize(Integer arraySize) { this.arraySize = arraySize; }
    public Integer getMin() { return min; }
    public void setMin(Integer min) { this.min = min; }
    public Integer getMax() { return max; }
    public void setMax(Integer max) { this.max = max; }
}
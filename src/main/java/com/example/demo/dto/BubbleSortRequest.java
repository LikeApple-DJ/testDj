package com.example.demo.dto;

import java.util.List;

public class BubbleSortRequest {
    private List<Integer> array;
    private String order;

    public List<Integer> getArray() { return array; }
    public void setArray(List<Integer> array) { this.array = array; }
    public String getOrder() { return order; }
    public void setOrder(String order) { this.order = order; }
}
package com.testdj.dto;

import java.util.List;

public class SortRequest {
    private List<Integer> array;

    public SortRequest() {}

    public SortRequest(List<Integer> array) {
        this.array = array;
    }

    public List<Integer> getArray() {
        return array;
    }

    public void setArray(List<Integer> array) {
        this.array = array;
    }
}
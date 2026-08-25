package com.testdj.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BubbleSortRequest {
    @NotNull(message = "array must not be null")
    private List<Integer> array;

    public BubbleSortRequest() {}

    public BubbleSortRequest(List<Integer> array) {
        this.array = array;
    }

    public List<Integer> getArray() { return array; }
    public void setArray(List<Integer> array) { this.array = array; }
}
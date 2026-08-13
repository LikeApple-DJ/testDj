package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class SortRequest {

    @NotNull(message = "array 不能为空")
    @Size(max = 10000, message = "array 长度不超过 10000")
    private List<Integer> array;

    public List<Integer> getArray() { return array; }
    public void setArray(List<Integer> array) { this.array = array; }
}

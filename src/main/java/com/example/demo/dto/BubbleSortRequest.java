package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 冒泡排序请求 DTO。
 */
public class BubbleSortRequest {

    @NotNull(message = "array 不能为 null")
    @NotEmpty(message = "array 不能为空")
    private List<Integer> array;

    @NotBlank(message = "order 不能为空，请指定 asc 或 desc")
    private String order;

    public List<Integer> getArray() { return array; }
    public void setArray(List<Integer> array) { this.array = array; }
    public String getOrder() { return order; }
    public void setOrder(String order) { this.order = order; }
}
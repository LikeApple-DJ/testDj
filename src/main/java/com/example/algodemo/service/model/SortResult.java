package com.example.algodemo.service.model;

import java.util.Arrays;

/**
 * 冒泡排序结果。
 */
public class SortResult {

    private int[] originalArray;
    private int[] sortedArray;
    private String order;

    public SortResult() {
    }

    public SortResult(int[] originalArray, int[] sortedArray, String order) {
        this.originalArray = originalArray;
        this.sortedArray = sortedArray;
        this.order = order;
    }

    public int[] getOriginalArray() {
        return originalArray;
    }

    public void setOriginalArray(int[] originalArray) {
        this.originalArray = originalArray;
    }

    public int[] getSortedArray() {
        return sortedArray;
    }

    public void setSortedArray(int[] sortedArray) {
        this.sortedArray = sortedArray;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "SortResult{"
                + "originalArray=" + Arrays.toString(originalArray)
                + ", sortedArray=" + Arrays.toString(sortedArray)
                + ", order='" + order + '\''
                + '}';
    }
}

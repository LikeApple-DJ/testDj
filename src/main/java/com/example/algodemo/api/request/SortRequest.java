package com.example.algodemo.api.request;

/**
 * 冒泡排序请求。
 */
public class SortRequest {

    private int[] array;
    private String order;

    public int[] getArray() {
        return array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}

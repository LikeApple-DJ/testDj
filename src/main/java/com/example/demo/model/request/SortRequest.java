package com.example.demo.model.request;

/**
 * 冒泡排序请求参数。
 */
public class SortRequest {

    /** 待排序的整数数组，必填 */
    private int[] array;

    public int[] getArray() {
        return array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }
}
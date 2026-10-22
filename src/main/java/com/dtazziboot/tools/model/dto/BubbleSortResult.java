package com.dtazziboot.tools.model.dto;

import java.io.Serializable;

/**
 * 冒泡排序出参。
 */
public class BubbleSortResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 原始数组。 */
    private int[] original;

    /** 排序后数组。 */
    private int[] sorted;

    /** 交换次数。 */
    private long swapCount;

    /** 排序方向。 */
    private String order;

    public int[] getOriginal() {
        return original;
    }

    public void setOriginal(int[] original) {
        this.original = original;
    }

    public int[] getSorted() {
        return sorted;
    }

    public void setSorted(int[] sorted) {
        this.sorted = sorted;
    }

    public long getSwapCount() {
        return swapCount;
    }

    public void setSwapCount(long swapCount) {
        this.swapCount = swapCount;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
package com.algorithm.demo.model.dto;

import java.util.List;

/**
 * 冒泡排序结果（包含排序后列表和交换次数）
 *
 * @author DTCoder
 */
public class SortResult {

    /** 排序后结果 */
    private final List<Integer> sorted;

    /** 交换次数 */
    private final int swapCount;

    public SortResult(List<Integer> sorted, int swapCount) {
        this.sorted = sorted;
        this.swapCount = swapCount;
    }

    public List<Integer> getSorted() {
        return sorted;
    }

    public int getSwapCount() {
        return swapCount;
    }
}

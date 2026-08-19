package com.algorithm.demo.model.dto;

import java.util.List;

/**
 * 冒泡排序响应数据
 *
 * @author DTCoder
 */
public class SortResponse {

    /** 原始输入列表 */
    private List<Integer> original;

    /** 排序后结果 */
    private List<Integer> sorted;

    /** 交换次数 */
    private Integer swapCount;

    public SortResponse() {
    }

    public SortResponse(List<Integer> original, List<Integer> sorted, Integer swapCount) {
        this.original = original;
        this.sorted = sorted;
        this.swapCount = swapCount;
    }

    public List<Integer> getOriginal() {
        return original;
    }

    public void setOriginal(List<Integer> original) {
        this.original = original;
    }

    public List<Integer> getSorted() {
        return sorted;
    }

    public void setSorted(List<Integer> sorted) {
        this.sorted = sorted;
    }

    public Integer getSwapCount() {
        return swapCount;
    }

    public void setSwapCount(Integer swapCount) {
        this.swapCount = swapCount;
    }
}

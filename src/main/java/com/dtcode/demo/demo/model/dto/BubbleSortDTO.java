package com.dtcode.demo.demo.model.dto;

import java.util.List;

/**
 * 冒泡排序响应数据
 *
 * @author DTCoder
 */
public class BubbleSortDTO {

    private List<Integer> original;
    private List<Integer> sorted;
    private String timestamp;

    public BubbleSortDTO() {
    }

    public BubbleSortDTO(List<Integer> original, List<Integer> sorted, String timestamp) {
        this.original = original;
        this.sorted = sorted;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BubbleSortDTO{original=" + original + ", sorted=" + sorted + ", timestamp='" + timestamp + "'}";
    }
}

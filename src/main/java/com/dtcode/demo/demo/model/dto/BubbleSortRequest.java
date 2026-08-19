package com.dtcode.demo.demo.model.dto;

import java.util.List;

/**
 * 冒泡排序请求参数
 *
 * @author DTCoder
 */
public class BubbleSortRequest {

    private List<Integer> numbers;

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    @Override
    public String toString() {
        return "BubbleSortRequest{numbers=" + numbers + "}";
    }
}

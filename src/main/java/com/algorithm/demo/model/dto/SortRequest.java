package com.algorithm.demo.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 冒泡排序请求
 *
 * @author DTCoder
 */
public class SortRequest {

    /** 待排序的整数列表 */
    @NotEmpty(message = "排序列表不能为空")
    @Size(max = 10000, message = "排序列表长度不能超过10000")
    private List<Integer> numbers;

    public SortRequest() {
    }

    public SortRequest(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }
}

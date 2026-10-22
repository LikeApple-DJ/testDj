package com.dtazziboot.tools.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * 冒泡排序请求参数。
 */
public class BubbleSortRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 待排序数组，必填。 */
    @NotEmpty(message = "nums cannot be empty")
    @Size(max = 1000, message = "nums too long")
    private int[] nums;

    /** 排序方向，ASC / DESC，默认 ASC。 */
    private String order;

    public int[] getNums() {
        return nums;
    }

    public void setNums(int[] nums) {
        this.nums = nums;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
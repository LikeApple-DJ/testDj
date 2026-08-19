package com.example.algodemo.service;

import com.example.algodemo.service.model.SortResult;

/**
 * 排序服务。
 */
public interface SortService {

    /**
     * 对整数数组执行冒泡排序。
     *
     * @param array 待排序整数数组
     * @param order 排序方向：ASC / DESC，为空时默认 ASC
     * @return 排序结果
     */
    SortResult bubbleSort(int[] array, String order);
}

package com.dtazziboot.tools.service;

import com.dtazziboot.tools.model.dto.BubbleSortResult;
import com.dtazziboot.tools.model.dto.HashResult;
import com.dtazziboot.tools.model.dto.HelloRequest;
import com.dtazziboot.tools.model.dto.HelloResult;

/**
 * 算法工具服务：helloworld / 哈希 / 冒泡排序。
 */
public interface ToolService {

    /**
     * 返回问候语。
     */
    HelloResult hello(HelloRequest request);

    /**
     * 对文本计算哈希摘要。
     *
     * @param text      待哈希文本，非空
     * @param algorithm 算法名，取值 MD5 / SHA256
     */
    HashResult hash(String text, String algorithm);

    /**
     * 对整型数组执行冒泡排序。
     *
     * @param nums  待排序数组，非空且长度不超过 1000
     * @param order 排序方向，ASC / DESC，空值默认 ASC
     */
    BubbleSortResult bubbleSort(int[] nums, String order);
}
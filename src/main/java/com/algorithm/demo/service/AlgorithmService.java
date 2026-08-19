package com.algorithm.demo.service;

import com.algorithm.demo.model.dto.SortResult;

import java.util.List;

/**
 * 算法服务接口
 *
 * @author DTCoder
 */
public interface AlgorithmService {

    /**
     * 执行 HelloWorld 算法
     *
     * @return 固定消息 "Hello World"
     */
    String hello();

    /**
     * 执行 SHA-256 哈希计算
     *
     * @param input 待计算的输入字符串
     * @return 哈希值（十六进制字符串）
     * @throws com.algorithm.demo.common.BusinessException 输入为空时抛出 ALGO_002
     */
    String hash(String input);

    /**
     * 执行冒泡排序（升序），返回排序结果和交换次数
     *
     * @param numbers 待排序的整数列表
     * @return SortResult 包含排序后的新列表和交换次数（不修改原始列表）
     * @throws com.algorithm.demo.common.BusinessException 列表为空时抛出 ALGO_004
     */
    SortResult bubbleSort(List<Integer> numbers);
}

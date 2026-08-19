package com.algorithm.demo.service;

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
     * @return HelloWorld 消息字符串（含时间戳）
     */
    String hello();

    /**
     * 执行 SHA-256 哈希计算
     *
     * @param input 待计算的输入字符串
     * @return 哈希值（十六进制字符串）
     * @throws IllegalArgumentException 输入为空或空白时抛出
     */
    String hash(String input);

    /**
     * 执行冒泡排序（升序）
     *
     * @param numbers 待排序的整数列表
     * @return 排序后的新列表（不修改原始列表）
     * @throws IllegalArgumentException 列表为空或 null 时抛出
     */
    List<Integer> bubbleSort(List<Integer> numbers);
}

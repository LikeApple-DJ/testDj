package com.example.demo.service;

import java.util.Map;

/**
 * Demo 演示模块服务接口。
 */
public interface DemoService {

    /**
     * 返回问候语。
     *
     * @return 问候语字符串
     */
    String hello();

    /**
     * 对输入字符串计算哈希值。
     *
     * @param input     原始字符串
     * @param algorithm 哈希算法（SHA-256 / MD5）
     * @return 哈希结果（十六进制字符串）
     */
    String hash(String input, String algorithm);

    /**
     * 对整数数组执行冒泡排序。
     *
     * @param array 待排序数组
     * @return 排序后数组
     */
    int[] bubbleSort(int[] array);

    /**
     * 导出结果数据。
     *
     * @param type   导出类型
     * @param format 导出格式
     * @param data   结果数据
     * @return 格式化后的字节数组
     */
    byte[] export(String type, String format, Map<String, Object> data);
}
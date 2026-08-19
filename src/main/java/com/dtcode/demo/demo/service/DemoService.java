package com.dtcode.demo.demo.service;

import com.dtcode.demo.demo.model.dto.BubbleSortDTO;
import com.dtcode.demo.demo.model.dto.HashDTO;
import com.dtcode.demo.demo.model.dto.HelloWorldDTO;

import java.util.List;

/**
 * 演示接口业务服务
 *
 * @author DTCoder
 */
public interface DemoService {

    /**
     * HelloWorld 问候
     *
     * @param name 名称，可为null
     * @return 问候结果
     */
    HelloWorldDTO helloWorld(String name);

    /**
     * SHA-256 哈希计算
     *
     * @param input 输入字符串
     * @return 哈希结果
     */
    HashDTO hash(String input);

    /**
     * 冒泡排序
     *
     * @param numbers 整数列表
     * @return 排序结果
     */
    BubbleSortDTO bubbleSort(List<Integer> numbers);
}

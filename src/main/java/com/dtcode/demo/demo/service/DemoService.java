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
     * 设置当前线程的调用者上下文（由 Controller 层调用）
     *
     * @param callerId 调用人ID
     */
    void setCallerContext(String callerId);

    /**
     * 清除当前线程的调用者上下文
     */
    void clearCallerContext();

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

    /**
     * 获取当前用户的最近执行结果缓存
     *
     * @param cacheKey 缓存键
     * @return 缓存的结果对象，不存在返回null
     */
    Object getCachedResult(String cacheKey);
}

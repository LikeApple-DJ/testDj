package com.dtcode.demo.export.service;

/**
 * 导出服务接口
 *
 * @author DTCoder
 */
public interface ExportService {

    /**
     * 导出 HelloWorld 结果为 CSV
     *
     * @return CSV 文件字节内容
     */
    byte[] exportHelloWorld();

    /**
     * 导出哈希算法结果为 CSV
     *
     * @return CSV 文件字节内容
     */
    byte[] exportHash();

    /**
     * 导出冒泡排序结果为 CSV
     *
     * @return CSV 文件字节内容
     */
    byte[] exportBubbleSort();
}

package com.algorithm.demo.service;

import com.algorithm.demo.common.AlgorithmType;

/**
 * 导出服务接口
 *
 * @author DTCoder
 */
public interface ExportService {

    /**
     * 根据算法类型导出结果（CSV 格式）
     *
     * @param type  算法类型
     * @param input 输入参数（HASH 和 SORT 类型需要）
     * @return CSV 文件字节数组
     * @throws IllegalArgumentException 参数不合法时抛出
     */
    byte[] exportResult(AlgorithmType type, String input);

    /**
     * 获取导出文件名
     *
     * @param type 算法类型
     * @return 文件名（含时间戳）
     */
    String getFileName(AlgorithmType type);
}

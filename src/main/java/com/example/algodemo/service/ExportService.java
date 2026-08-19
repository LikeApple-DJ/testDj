package com.example.algodemo.service;

import com.example.algodemo.service.model.ExportResult;

import java.util.Map;

/**
 * 导出服务。
 */
public interface ExportService {

    /**
     * 导出指定算法的结果。
     *
     * @param type    导出类型：hello / hash / bubbleSort
     * @param format  导出格式：CSV / JSON，为空时默认 CSV
     * @param params  对应接口的请求参数
     * @return 导出结果
     */
    ExportResult export(String type, String format, Map<String, Object> params);
}

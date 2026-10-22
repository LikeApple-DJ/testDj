package com.dtazziboot.tools.service;

import com.dtazziboot.tools.model.dto.ExportFile;
import com.dtazziboot.tools.model.dto.ExportRequest;

/**
 * 导出服务：按类型导出各 Tab 页面展示结果。
 */
public interface ExportService {

    /**
     * 按 type 生成待导出文件。
     */
    ExportFile export(ExportRequest request);
}
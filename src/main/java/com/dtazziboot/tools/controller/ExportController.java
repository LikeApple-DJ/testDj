package com.dtazziboot.tools.controller;

import com.dtazziboot.tools.model.dto.ExportFile;
import com.dtazziboot.tools.model.dto.ExportRequest;
import com.dtazziboot.tools.service.ExportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 导出接口（W04）：按类型导出各 Tab 展示结果，返回文件流。
 */
@RestController
@RequestMapping("/api/tools")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody ExportRequest request) {
        ExportFile file = exportService.export(request);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"")
                .header("Content-Type", file.getContentType())
                .body(file.getData());
    }
}
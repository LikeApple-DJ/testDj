package com.dt.example.web;

import com.dt.example.export.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 导出接口：接收前端页面展示结果，转换为指定格式文件返回。
 */
@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody ExportRequest request) throws Exception {
        ExportService.ExportFile file = exportService.export(
            request.format(), request.page(), request.rows());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.filename() + "\"")
            .contentType(MediaType.parseMediaType(file.contentType()))
            .body(file.content());
    }

    public record ExportRequest(String format, String page, List<Map<String, Object>> rows) {
    }
}
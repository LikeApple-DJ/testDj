package com.example.algodemo.api.controller;

import com.example.algodemo.api.request.ExportRequest;
import com.example.algodemo.common.response.ApiResponse;
import com.example.algodemo.service.ExportService;
import com.example.algodemo.service.model.ExportResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * 导出接口。
 */
@RestController
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping({"/api/export", "/openapi/export"})
    public ApiResponse<ExportResult> export(@RequestBody ExportRequest request) {
        ExportResult result = exportService.export(request.getType(), request.getFormat(), request.getParams());
        return ApiResponse.success(result);
    }

    @PostMapping({"/api/export/download", "/openapi/export/download"})
    public ResponseEntity<byte[]> download(@RequestBody ExportRequest request) {
        ExportResult result = exportService.export(request.getType(), request.getFormat(), request.getParams());
        String contentType = result.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        String contentDisposition = "attachment; filename=\"" + result.getFilename() + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.parseMediaType(contentType))
                .body(result.getContent().getBytes(StandardCharsets.UTF_8));
    }
}

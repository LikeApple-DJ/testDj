package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import com.example.demo.dto.ExportRequest;
import com.example.demo.service.ExportService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Excel 导出接口控制器。
 */
@RestController
@RequestMapping("/api")
public class ExportController {

    private static final Logger log = LoggerFactory.getLogger(ExportController.class);

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * 导出指定类型的数据为 Excel 文件。
     */
    @PostMapping("/export")
    public ResponseEntity<?> export(@Valid @RequestBody ExportRequest request) {
        try {
            byte[] bytes = exportService.export(request.getType(), request.getData());
            String timestamp = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String filename = "export-" + request.getType() + "-" + timestamp + ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(filename, StandardCharsets.UTF_8).build());
            headers.setContentLength(bytes.length);

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("导出参数非法: type={}", request.getType(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResult.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Excel 生成失败: type={}", request.getType(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResult.error(500, "Excel 生成失败: " + e.getMessage()));
        }
    }
}
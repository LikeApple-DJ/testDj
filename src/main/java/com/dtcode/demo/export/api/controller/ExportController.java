package com.dtcode.demo.export.api.controller;

import com.dtcode.demo.export.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 导出控制器
 *
 * @author DTCoder
 */
@RestController
@RequestMapping("/api/export")
public class ExportController {

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/helloworld")
    public ResponseEntity<byte[]> exportHelloWorld() {
        byte[] csvContent = exportService.exportHelloWorld();
        String filename = "helloworld_" + LocalDateTime.now().format(FILE_DATE_FORMAT) + ".csv";
        return buildCsvResponse(csvContent, filename);
    }

    @GetMapping("/hash")
    public ResponseEntity<byte[]> exportHash() {
        byte[] csvContent = exportService.exportHash();
        String filename = "hash_" + LocalDateTime.now().format(FILE_DATE_FORMAT) + ".csv";
        return buildCsvResponse(csvContent, filename);
    }

    @GetMapping("/bubble-sort")
    public ResponseEntity<byte[]> exportBubbleSort() {
        byte[] csvContent = exportService.exportBubbleSort();
        String filename = "bubble-sort_" + LocalDateTime.now().format(FILE_DATE_FORMAT) + ".csv";
        return buildCsvResponse(csvContent, filename);
    }

    private ResponseEntity<byte[]> buildCsvResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.setContentLength(content.length);
        return ResponseEntity.ok().headers(headers).body(content);
    }
}

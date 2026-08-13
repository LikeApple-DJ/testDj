package com.example.demo.controller;

import com.example.demo.exception.BusinessException;
import com.example.demo.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@RestController
@RequestMapping("/api/demo")
public class ExportController {

    private static final Set<String> VALID_TYPES = Set.of("hello", "hash", "bubble-sort");

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(defaultValue = "hello") String type,
            @RequestParam(defaultValue = "csv") String format) {

        if (!VALID_TYPES.contains(type)) {
            throw new BusinessException("EXPORT_001", "type 参数不合法，必须为 hello/hash/bubble-sort");
        }

        String csvContent = exportService.exportToCsv(type);
        String filename = type + "-export.csv";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
            .body(csvContent.getBytes(StandardCharsets.UTF_8));
    }
}

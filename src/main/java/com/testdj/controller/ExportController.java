package com.testdj.controller;

import com.testdj.dto.ExportRequest;
import com.testdj.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @PostMapping
    public ResponseEntity<byte[]> export(@RequestBody ExportRequest request) {
        String tab = request.getTab() != null ? request.getTab() : "hello";

        // Build sample result data based on tab type
        Map<String, Object> resultData = buildSampleData(tab);
        byte[] pdfBytes = exportService.exportTabResult(tab, resultData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", tab + "_result.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    private Map<String, Object> buildSampleData(String tab) {
        return switch (tab) {
            case "hello" -> Map.of(
                    "Tab", "Hello World",
                    "Input", "World",
                    "Message", "Hello, World!"
            );
            case "hash" -> Map.of(
                    "Tab", "SHA-256 Hash",
                    "Input", "hello",
                    "Algorithm", "SHA-256",
                    "Hash Result", "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
            );
            case "sort" -> Map.of(
                    "Tab", "Bubble Sort",
                    "Original Array", "[3, 1, 4, 1, 5]",
                    "Sorted Array", "[1, 1, 3, 4, 5]",
                    "Length", "5"
            );
            default -> Map.of("Tab", "Unknown", "Info", "No data available");
        };
    }
}
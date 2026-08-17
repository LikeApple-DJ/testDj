package com.testdj.controller;

import com.testdj.dto.ExportRequest;
import com.testdj.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @PostMapping
    public ResponseEntity<byte[]> export(@RequestBody ExportRequest request) {
        String tab = request.getTab() != null ? request.getTab() : "hello";
        Map<String, Object> resultData = request.getResultData();

        // If no result data provided, use empty map (will show "No data available")
        if (resultData == null) {
            resultData = Map.of();
        }

        byte[] pdfBytes = exportService.exportTabResult(tab, resultData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", tab + "_result.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
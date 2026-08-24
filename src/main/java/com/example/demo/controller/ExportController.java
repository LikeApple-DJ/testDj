package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam("tab") String tab,
            @RequestParam(value = "callerName", required = false) String callerName) {

        byte[] csvBytes = exportService.exportCsv(tab, callerName);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String filename = "export_" + tab + "_" + timestamp + ".csv";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.setContentLength(csvBytes.length);

        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
}
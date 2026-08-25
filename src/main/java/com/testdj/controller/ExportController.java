package com.testdj.controller;

import com.testdj.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam("type") String type) {
        // Validate type
        if (!"hello".equals(type) && !"hash".equals(type) && !"bubble".equals(type)) {
            return ResponseEntity.badRequest()
                    .body("{\"code\":1,\"message\":\"Invalid type: " + type + ". Supported: hello, hash, bubble\"}".getBytes(StandardCharsets.UTF_8));
        }

        String csv = exportService.exportCsv(type);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", type + "_export.csv");

        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}
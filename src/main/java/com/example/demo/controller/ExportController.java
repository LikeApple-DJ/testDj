package com.example.demo.controller;
import com.example.demo.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Set;
@RestController
@RequestMapping("/api")
public class ExportController {
    private final ExportService exportService;
    private static final Set<String> VALID_TYPES = Set.of("helloworld", "hash", "bubblesort");
    public ExportController(ExportService exportService) { this.exportService = exportService; }
    @GetMapping("/export")
    public ResponseEntity<?> export(@RequestParam String type) {
        if (!VALID_TYPES.contains(type)) {
            return ResponseEntity.badRequest().body(Map.of("code", "EXPORT_001", "message", "不支持的导出类型"));
        }
        byte[] excel = exportService.generateExcel(type);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + type + "_export.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
package com.dtcode.demo.export.api.controller;

import com.dtcode.demo.demo.service.DemoService;
import com.dtcode.demo.export.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 导出控制器
 *
 * @author DTCoder
 */
@RestController
@RequestMapping("/api/export")
public class ExportController {

    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final ZoneId ZONE_SHANGHAI = ZoneId.of("Asia/Shanghai");

    private final ExportService exportService;
    private final DemoService demoService;

    public ExportController(ExportService exportService, DemoService demoService) {
        this.exportService = exportService;
        this.demoService = demoService;
    }

    @GetMapping("/helloworld")
    public ResponseEntity<byte[]> exportHelloWorld(HttpServletRequest httpRequest) {
        String callerId = resolveCallerId(httpRequest);
        demoService.setCallerContext(callerId);
        try {
            byte[] csvContent = exportService.exportHelloWorld();
            String filename = "helloworld_"
                    + LocalDateTime.now(ZONE_SHANGHAI).format(FILE_DATE_FORMAT) + ".csv";
            return buildCsvResponse(csvContent, filename);
        } finally {
            demoService.clearCallerContext();
        }
    }

    @GetMapping("/hash")
    public ResponseEntity<byte[]> exportHash(HttpServletRequest httpRequest) {
        String callerId = resolveCallerId(httpRequest);
        demoService.setCallerContext(callerId);
        try {
            byte[] csvContent = exportService.exportHash();
            String filename = "hash_"
                    + LocalDateTime.now(ZONE_SHANGHAI).format(FILE_DATE_FORMAT) + ".csv";
            return buildCsvResponse(csvContent, filename);
        } finally {
            demoService.clearCallerContext();
        }
    }

    @GetMapping("/bubble-sort")
    public ResponseEntity<byte[]> exportBubbleSort(HttpServletRequest httpRequest) {
        String callerId = resolveCallerId(httpRequest);
        demoService.setCallerContext(callerId);
        try {
            byte[] csvContent = exportService.exportBubbleSort();
            String filename = "bubble-sort_"
                    + LocalDateTime.now(ZONE_SHANGHAI).format(FILE_DATE_FORMAT) + ".csv";
            return buildCsvResponse(csvContent, filename);
        } finally {
            demoService.clearCallerContext();
        }
    }

    private ResponseEntity<byte[]> buildCsvResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.setContentLength(content.length);
        return ResponseEntity.ok().headers(headers).body(content);
    }

    /**
     * 从请求头解析调用人ID
     */
    private String resolveCallerId(HttpServletRequest request) {
        String callerId = request.getHeader("X-Caller-Id");
        return (callerId != null && !callerId.trim().isEmpty()) ? callerId.trim() : "UNKNOWN";
    }
}

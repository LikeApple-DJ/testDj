package com.testdj.demo.export;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;

@RestController
@RequestMapping("/api/v1/demo")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping("/export")
    public void export(@RequestBody ExportRequest request, HttpServletResponse response) throws Exception {
        byte[] data = exportService.export(request.tab(), request.format());
        String extension = request.format().equalsIgnoreCase("excel") ? "xlsx" : "csv";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"demo-export." + extension + "\"");
        try (OutputStream out = response.getOutputStream()) {
            out.write(data);
            out.flush();
        }
    }
}
package com.example.cost.controller;

import com.example.cost.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cost")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping("/export")
    public void export(@RequestBody Map<String, Object> params, HttpServletResponse response) {
        exportService.export(params, response);
    }
}
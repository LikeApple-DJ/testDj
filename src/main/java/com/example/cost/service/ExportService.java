package com.example.cost.service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ExportService {
    void export(Map<String, Object> params, HttpServletResponse response);
}
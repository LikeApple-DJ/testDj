package com.example.demo.service;

import com.example.demo.repository.InvocationLogRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ExportService {

    private final InvocationLogRepository logRepository;

    public ExportService(InvocationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public byte[] exportLogs(String tab) throws IOException {
        List<Object[]> raw;
        switch (tab.toLowerCase()) {
            case "helloworld" -> raw = logRepository.countGroupByApi();
            case "hash"      -> raw = logRepository.countGroupByApi();
            case "bubblesort"-> raw = logRepository.countGroupByApi();
            default -> raw = logRepository.countGroupByApi();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(tab != null ? tab : "export");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Key");
            header.createCell(1).setCellValue("Count");

            int rowIdx = 1;
            for (Object[] row : raw) {
                Row r = sheet.createRow(rowIdx++);
                r.createCell(0).setCellValue(String.valueOf(row[0]));
                r.createCell(1).setCellValue(((Number) row[1]).longValue());
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }
}
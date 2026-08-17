package com.example.demo.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ExportService {

    public byte[] generateHelloExcel() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hello World");
            // 表头
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Tab 名称");
            header.createCell(1).setCellValue("数据内容");
            header.createCell(2).setCellValue("时间戳");

            // 数据行
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("Hello World");
            row.createCell(1).setCellValue("Hello World!");
            row.createCell(2).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // 自动调整列宽
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateHashExcel(String input, String hash) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("SHA-256 哈希");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Tab 名称");
            header.createCell(1).setCellValue("数据内容");
            header.createCell(2).setCellValue("时间戳");

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("SHA-256 哈希");
            row.createCell(1).setCellValue("输入: " + input + " | 哈希值: " + hash);
            row.createCell(2).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // 自动调整列宽
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateSortExcel(int[] original, int[] sorted, int swaps) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("冒泡排序");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Tab 名称");
            header.createCell(1).setCellValue("数据内容");
            header.createCell(2).setCellValue("时间戳");

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("冒泡排序");
            row.createCell(1).setCellValue("原始数组: " + java.util.Arrays.toString(original)
                + " | 排序后: " + java.util.Arrays.toString(sorted)
                + " | 交换次数: " + swaps);
            row.createCell(2).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // 自动调整列宽
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
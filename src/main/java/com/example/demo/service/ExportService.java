package com.example.demo.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ExportService {

    public byte[] export(String type, Object data) {
        if (!"helloworld".equals(type) && !"hash".equals(type) && !"bubblesort".equals(type)) {
            throw new IllegalArgumentException("不支持的导出类型: " + type);
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            switch (type) {
                case "helloworld":
                    exportHelloWorld(workbook, data);
                    break;
                case "hash":
                    exportHash(workbook, data);
                    break;
                case "bubblesort":
                    exportBubbleSort(workbook, data);
                    break;
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Excel 生成失败", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void exportHelloWorld(Workbook wb, Object data) {
        Sheet sheet = wb.createSheet("HelloWorld");
        Map<String, Object> map = (Map<String, Object>) data;
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("消息");
        header.createCell(1).setCellValue("时间戳");
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(String.valueOf(map.get("message")));
        row.createCell(1).setCellValue(String.valueOf(map.get("timestamp")));
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    @SuppressWarnings("unchecked")
    private void exportHash(Workbook wb, Object data) {
        Sheet sheet = wb.createSheet("Hash");
        Map<String, Object> map = (Map<String, Object>) data;
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("输入");
        header.createCell(1).setCellValue("算法");
        header.createCell(2).setCellValue("哈希值");
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(String.valueOf(map.get("input")));
        row.createCell(1).setCellValue(String.valueOf(map.get("algorithm")));
        row.createCell(2).setCellValue(String.valueOf(map.get("hash")));
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    @SuppressWarnings("unchecked")
    private void exportBubbleSort(Workbook wb, Object data) {
        Map<String, Object> map = (Map<String, Object>) data;

        // Sheet 1: Results
        Sheet resultSheet = wb.createSheet("排序结果");
        Row header = resultSheet.createRow(0);
        header.createCell(0).setCellValue("原始数组");
        header.createCell(1).setCellValue("排序结果");
        header.createCell(2).setCellValue("比较次数");
        Row row = resultSheet.createRow(1);
        row.createCell(0).setCellValue(String.valueOf(map.get("original")));
        row.createCell(1).setCellValue(String.valueOf(map.get("sorted")));
        row.createCell(2).setCellValue(String.valueOf(map.get("comparisons")));
        resultSheet.autoSizeColumn(0);
        resultSheet.autoSizeColumn(1);

        // Sheet 2: Steps
        Sheet stepsSheet = wb.createSheet("排序步骤");
        Row stepsHeader = stepsSheet.createRow(0);
        stepsHeader.createCell(0).setCellValue("轮次");
        stepsHeader.createCell(1).setCellValue("数组状态");
        List<Map<String, Object>> steps = (List<Map<String, Object>>) map.get("steps");
        if (steps != null) {
            int rowIdx = 1;
            for (Map<String, Object> step : steps) {
                Row stepRow = stepsSheet.createRow(rowIdx++);
                stepRow.createCell(0).setCellValue(((Number) step.get("round")).intValue());
                stepRow.createCell(1).setCellValue(String.valueOf(step.get("array")));
            }
        }
        stepsSheet.autoSizeColumn(0);
        stepsSheet.autoSizeColumn(1);
    }
}
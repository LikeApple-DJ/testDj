package com.example.demo.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Excel 导出服务，将指定类型的数据生成 .xlsx 文件。
 */
@Service
public class ExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportService.class);

    /**
     * 导出指定类型的数据为 Excel 字节数组。
     *
     * @param type 导出类型：helloworld / hash / bubblesort
     * @param data 导出数据
     * @return Excel 文件的字节数组
     * @throws IllegalArgumentException 如果 type 不支持
     */
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
        } catch (Exception e) {
            log.error("Excel 生成失败: type={}", type, e);
            throw new RuntimeException("Excel 生成失败", e);
        }
    }

    private void exportHelloWorld(Workbook wb, Object data) {
        if (!(data instanceof Map)) {
            throw new IllegalArgumentException("helloworld 导出数据格式错误，期望 Map 类型");
        }
        Map<?, ?> raw = (Map<?, ?>) data;
        Sheet sheet = wb.createSheet("HelloWorld");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("消息");
        header.createCell(1).setCellValue("时间戳");
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(String.valueOf(raw.get("message")));
        row.createCell(1).setCellValue(String.valueOf(raw.get("timestamp")));
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void exportHash(Workbook wb, Object data) {
        if (!(data instanceof Map)) {
            throw new IllegalArgumentException("hash 导出数据格式错误，期望 Map 类型");
        }
        Map<?, ?> raw = (Map<?, ?>) data;
        Sheet sheet = wb.createSheet("Hash");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("输入");
        header.createCell(1).setCellValue("算法");
        header.createCell(2).setCellValue("哈希值");
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(String.valueOf(raw.get("input")));
        row.createCell(1).setCellValue(String.valueOf(raw.get("algorithm")));
        row.createCell(2).setCellValue(String.valueOf(raw.get("hash")));
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    private void exportBubbleSort(Workbook wb, Object data) {
        if (!(data instanceof Map)) {
            throw new IllegalArgumentException("bubblesort 导出数据格式错误，期望 Map 类型");
        }
        Map<?, ?> map = (Map<?, ?>) data;

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
        Object stepsObj = map.get("steps");
        if (stepsObj instanceof List) {
            List<?> rawSteps = (List<?>) stepsObj;
            int rowIdx = 1;
            for (Object stepObj : rawSteps) {
                if (!(stepObj instanceof Map)) continue;
                Map<?, ?> step = (Map<?, ?>) stepObj;
                Row stepRow = stepsSheet.createRow(rowIdx++);
                Object roundObj = step.get("round");
                stepRow.createCell(0).setCellValue(roundObj instanceof Number ? ((Number) roundObj).intValue() : 0);
                stepRow.createCell(1).setCellValue(String.valueOf(step.get("array")));
            }
        }
        stepsSheet.autoSizeColumn(0);
        stepsSheet.autoSizeColumn(1);
    }
}
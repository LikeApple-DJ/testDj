package com.testdj.demo.export;

import com.testdj.demo.exception.BusinessException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ExportService {

    public byte[] export(String tab, String format) {
        List<String[]> rows = buildRows(tab);
        return switch (format.toLowerCase()) {
            case "csv" -> toCsv(rows);
            case "excel" -> toExcel(rows);
            default -> throw new BusinessException(400, "unsupported format: " + format);
        };
    }

    private List<String[]> buildRows(String tab) {
        return switch (tab) {
            case "hello" -> List.of(new String[]{"Hello, World!"});
            case "hash" -> List.of(
                    new String[]{"algorithm", "original", "hash"},
                    new String[]{"SHA-256", "demo", "hashValue"});
            case "bubble" -> List.of(
                    new String[]{"input", "output"},
                    new String[]{"[3,1,4]", "[1,3,4]"});
            case "all" -> List.of(
                    new String[]{"tab", "result"},
                    new String[]{"hello", "Hello, World!"},
                    new String[]{"hash", "hashValue"},
                    new String[]{"bubble", "[1,3,4]"});
            default -> throw new BusinessException(400, "unknown tab: " + tab);
        };
    }

    private byte[] toCsv(List<String[]> rows) {
        StringBuilder sb = new StringBuilder();
        for (String[] row : rows) {
            sb.append(String.join(",", row)).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] toExcel(List<String[]> rows) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("export");
            int rowIdx = 0;
            for (String[] row : rows) {
                Row excelRow = sheet.createRow(rowIdx++);
                for (int i = 0; i < row.length; i++) {
                    excelRow.createCell(i).setCellValue(row[i]);
                }
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BusinessException(500, "failed to generate excel");
        }
    }
}
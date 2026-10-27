package com.dt.example.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 导出服务：JSON / CSV / XLSX 三种格式。
 */
@Service
public class ExportService {

    private final ObjectMapper objectMapper;

    public ExportService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public record ExportFile(byte[] content, String filename, String contentType) {
    }

    public ExportFile export(String format, String page, List<Map<String, Object>> rows)
        throws Exception {
        if (format == null || format.isBlank()) {
            throw new IllegalArgumentException("unsupported format: " + format);
        }
        return switch (format.toLowerCase(Locale.ROOT)) {
            case "json" -> new ExportFile(
                objectMapper.writeValueAsBytes(rows == null ? List.of() : rows),
                name(page, "json"), "application/json;charset=UTF-8");
            case "csv" -> new ExportFile(toCsv(rows), name(page, "csv"),
                "text/csv;charset=UTF-8");
            case "xlsx" -> new ExportFile(toXlsx(rows), name(page, "xlsx"),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            default -> throw new IllegalArgumentException("unsupported format: " + format);
        };
    }

    private String name(String page, String ext) {
        return (page == null || page.isBlank() ? "export" : page) + "." + ext;
    }

    private LinkedHashSet<String> headers(List<Map<String, Object>> rows) {
        LinkedHashSet<String> headers = new LinkedHashSet<>();
        if (rows == null) {
            return headers;
        }
        for (Map<String, Object> row : rows) {
            if (row != null) {
                headers.addAll(row.keySet());
            }
        }
        return headers;
    }

    private byte[] toCsv(List<Map<String, Object>> rows) {
        LinkedHashSet<String> headers = headers(rows);
        StringBuilder sb = new StringBuilder("\uFEFF"); // BOM 让 Excel 正确识别 UTF-8
        sb.append(String.join(",", headers)).append("\n");
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                List<String> cells = new ArrayList<>();
                for (String h : headers) {
                    Object v = row == null ? null : row.get(h);
                    cells.add(escape(v == null ? "" : String.valueOf(v)));
                }
                sb.append(String.join(",", cells)).append("\n");
            }
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escape(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private byte[] toXlsx(List<Map<String, Object>> rows) throws Exception {
        LinkedHashSet<String> headers = headers(rows);
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("result");
            Row header = sheet.createRow(0);
            int c = 0;
            for (String h : headers) {
                header.createCell(c++).setCellValue(h);
            }
            int r = 1;
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Row xr = sheet.createRow(r++);
                    c = 0;
                    for (String h : headers) {
                        Object v = row == null ? null : row.get(h);
                        Cell cell = xr.createCell(c++);
                        if (v instanceof Number n) {
                            cell.setCellValue(n.doubleValue());
                        } else {
                            cell.setCellValue(v == null ? "" : String.valueOf(v));
                        }
                    }
                }
            }
            wb.write(out);
            return out.toByteArray();
        }
    }
}
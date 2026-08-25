package com.testdj.demo.export;

import com.testdj.demo.common.ErrorCode;
import com.testdj.demo.exception.BusinessException;
import com.testdj.demo.hash.HashRequest;
import com.testdj.demo.hash.HashResponse;
import com.testdj.demo.hash.HashService;
import com.testdj.demo.sort.BubbleSortService;
import com.testdj.demo.sort.SortRequest;
import com.testdj.demo.sort.SortResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);

    private final HashService hashService;
    private final BubbleSortService bubbleSortService;

    public ExportService(HashService hashService, BubbleSortService bubbleSortService) {
        this.hashService = hashService;
        this.bubbleSortService = bubbleSortService;
    }

    public byte[] export(ExportRequest request) {
        String format = request.format();
        if (format == null || format.isBlank()) {
            throw new BusinessException(ErrorCode.EXPORT_UNSUPPORTED_FORMAT,
                    ErrorCode.EXPORT_UNSUPPORTED_FORMAT_MSG + ": format is required");
        }
        String tab = request.tab();
        if (tab == null || tab.isBlank()) {
            throw new BusinessException(ErrorCode.EXPORT_UNKNOWN_TAB,
                    ErrorCode.EXPORT_UNKNOWN_TAB_MSG + ": tab is required");
        }
        List<String[]> rows = buildRows(request);
        return switch (format.toLowerCase()) {
            case "csv" -> toCsv(rows);
            case "excel" -> toExcel(rows);
            default -> throw new BusinessException(ErrorCode.EXPORT_UNSUPPORTED_FORMAT,
                    ErrorCode.EXPORT_UNSUPPORTED_FORMAT_MSG + ": " + format);
        };
    }

    private List<String[]> buildRows(ExportRequest request) {
        String tab = request.tab();
        return switch (tab) {
            case "hello" -> List.of(new String[]{"Hello, World!"});
            case "hash" -> buildHashRows(request);
            case "bubble" -> buildBubbleRows(request);
            case "all" -> buildAllRows(request);
            default -> throw new BusinessException(ErrorCode.EXPORT_UNKNOWN_TAB,
                    ErrorCode.EXPORT_UNKNOWN_TAB_MSG + ": " + tab);
        };
    }

    private List<String[]> buildHashRows(ExportRequest request) {
        String content = request.content() == null ? "demo" : request.content();
        String algorithm = request.algorithm() == null ? "SHA-256" : request.algorithm();
        HashResponse response = hashService.hash(new HashRequest(algorithm, content));
        return List.of(
                new String[]{"algorithm", "original", "hash"},
                new String[]{response.algorithm(), response.original(), response.hash()}
        );
    }

    private List<String[]> buildBubbleRows(ExportRequest request) {
        List<Integer> numbers = request.numbers() == null ? List.of(3, 1, 4, 1, 5) : request.numbers();
        boolean ascending = request.ascending() == null || request.ascending();
        boolean unique = request.unique() != null && request.unique();
        SortResponse response = bubbleSortService.sort(new SortRequest(numbers, ascending, unique));
        return List.of(
                new String[]{"input", "output"},
                new String[]{response.input().toString(), response.output().toString()}
        );
    }

    private List<String[]> buildAllRows(ExportRequest request) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"tab", "result"});
        rows.add(new String[]{"hello", "Hello, World!"});
        List<String[]> hashRows = buildHashRows(request);
        String[] hashData = hashRows.get(1);
        rows.add(new String[]{"hash", hashData[2]});
        List<String[]> bubbleRows = buildBubbleRows(request);
        String[] bubbleData = bubbleRows.get(1);
        rows.add(new String[]{"bubble", bubbleData[1]});
        return rows;
    }

    private byte[] toCsv(List<String[]> rows) {
        StringBuilder sb = new StringBuilder();
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(escapeCsv(row[i]));
            }
            sb.append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private byte[] toExcel(List<String[]> rows) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
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
            LOGGER.error("failed to generate excel", e);
            throw new BusinessException(ErrorCode.EXPORT_GENERATE_EXCEL_FAILED,
                    ErrorCode.EXPORT_GENERATE_EXCEL_FAILED_MSG);
        }
    }
}

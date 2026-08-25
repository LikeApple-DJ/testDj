package com.testdj.demo.export;

import com.testdj.demo.common.ErrorCode;
import com.testdj.demo.exception.BusinessException;
import com.testdj.demo.hash.HashService;
import com.testdj.demo.sort.BubbleSortService;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportServiceTest {

    private final ExportService exportService = new ExportService(new HashService(), new BubbleSortService());

    @Test
    void shouldThrowWhenFormatIsNull() {
        ExportRequest request = new ExportRequest("hello", null, null, null, null, null, null);
        BusinessException ex = assertThrows(BusinessException.class, () -> exportService.export(request));
        assertEquals(ErrorCode.EXPORT_UNSUPPORTED_FORMAT, ex.getCode());
    }

    @Test
    void shouldThrowWhenTabIsNull() {
        ExportRequest request = new ExportRequest(null, "csv", null, null, null, null, null);
        BusinessException ex = assertThrows(BusinessException.class, () -> exportService.export(request));
        assertEquals(ErrorCode.EXPORT_UNKNOWN_TAB, ex.getCode());
    }

    @Test
    void shouldEscapeSpecialCharactersInCsv() {
        ExportRequest request = new ExportRequest("hash", "csv", "hello,world", null, null, null, null);
        byte[] data = exportService.export(request);
        String csv = new String(data, StandardCharsets.UTF_8);
        assertTrue(csv.contains("\"hello,world\""));
    }
}

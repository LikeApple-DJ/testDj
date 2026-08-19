package com.example.algodemo.service.impl;

import com.example.algodemo.common.exception.BusinessException;
import com.example.algodemo.service.HashService;
import com.example.algodemo.service.HelloWorldService;
import com.example.algodemo.service.SortService;
import com.example.algodemo.service.model.ExportResult;
import com.example.algodemo.service.model.HashResult;
import com.example.algodemo.service.model.SortResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {

    @Mock
    private HelloWorldService helloWorldService;

    @Mock
    private HashService hashService;

    @Mock
    private SortService sortService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ExportServiceImpl exportService;

    @BeforeEach
    void setUp() {
        exportService = new ExportServiceImpl(helloWorldService, hashService, sortService, objectMapper);
    }

    @Test
    void should_exportHelloCsv_when_typeIsHelloAndFormatIsCsv() {
        // Arrange
        when(helloWorldService.sayHello(anyString())).thenReturn("Hello, Test!");
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Test");

        // Act
        ExportResult result = exportService.export("hello", "CSV", params);

        // Assert
        assertThat(result.getFilename()).isEqualTo("export_hello.csv");
        assertThat(result.getContentType()).isEqualTo("text/csv;charset=UTF-8");
        assertThat(result.getContent()).contains("hello,Hello, Test!");
    }

    @Test
    void should_exportHashJson_when_typeIsHashAndFormatIsJson() {
        // Arrange
        HashResult hashResult = new HashResult("SHA256", "hello", "digest123");
        when(hashService.hash(anyString(), anyString())).thenReturn(hashResult);
        Map<String, Object> params = new HashMap<>();
        params.put("algorithm", "SHA256");
        params.put("content", "hello");

        // Act
        ExportResult result = exportService.export("hash", "JSON", params);

        // Assert
        assertThat(result.getFilename()).isEqualTo("export_hash.json");
        assertThat(result.getContentType()).isEqualTo("application/json");
        assertThat(result.getContent()).contains("\"algorithm\"").contains("SHA256");
    }

    @Test
    void should_exportBubbleSortCsv_when_typeIsBubbleSort() {
        // Arrange
        SortResult sortResult = new SortResult(new int[]{3, 1, 2}, new int[]{1, 2, 3}, "ASC");
        when(sortService.bubbleSort((int[]) any(), anyString())).thenReturn(sortResult);
        Map<String, Object> params = new HashMap<>();
        params.put("array", Arrays.asList(3, 1, 2));
        params.put("order", "ASC");

        // Act
        ExportResult result = exportService.export("bubbleSort", "CSV", params);

        // Assert
        assertThat(result.getFilename()).isEqualTo("export_bubbleSort.csv");
        assertThat(result.getContent()).contains("originalArray,sortedArray,order");
    }

    @Test
    void should_throwBusinessException_when_typeIsUnsupported() {
        // Act & Assert
        assertThatThrownBy(() -> exportService.export("unknown", "CSV", new HashMap<>()))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo("ALG_001");
                });
    }

    @Test
    void should_defaultToCsv_when_formatIsNull() {
        // Arrange
        when(helloWorldService.sayHello(anyString())).thenReturn("Hello, World!");

        // Act
        ExportResult result = exportService.export("hello", null, new HashMap<>());

        // Assert
        assertThat(result.getFilename()).isEqualTo("export_hello.csv");
    }
}

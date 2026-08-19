package com.algorithm.demo.service;

import com.algorithm.demo.common.AlgorithmType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExportService 单元测试
 * 覆盖场景：正常导出、参数校验、异常处理
 */
@DisplayName("ExportService 单元测试")
class ExportServiceTest {

    private ExportService exportService;
    private AlgorithmService algorithmService;

    @BeforeEach
    void setUp() {
        algorithmService = new AlgorithmServiceImpl();
        exportService = new ExportServiceImpl(algorithmService);
    }

    // ==================== exportResult() 测试 ====================

    @Test
    @DisplayName("exportResult - HELLO 类型正常导出 CSV")
    void should_returnCsvBytes_when_typeIsHello() {
        // Act
        byte[] result = exportService.exportResult(AlgorithmType.HELLO, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String csvContent = new String(result);
        assertTrue(csvContent.contains("Hello World"));
    }

    @Test
    @DisplayName("exportResult - HASH 类型正常导出 CSV")
    void should_returnCsvBytes_when_typeIsHash() {
        // Act
        byte[] result = exportService.exportResult(AlgorithmType.HASH, "test");

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String csvContent = new String(result);
        assertTrue(csvContent.contains("test"));
        assertTrue(csvContent.contains("SHA-256"));
    }

    @Test
    @DisplayName("exportResult - SORT 类型正常导出 CSV")
    void should_returnCsvBytes_when_typeIsSort() {
        // Act
        byte[] result = exportService.exportResult(AlgorithmType.SORT, "5,3,8,1,2");

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        String csvContent = new String(result);
        assertTrue(csvContent.contains("排序"));
    }

    @Test
    @DisplayName("exportResult - HASH 类型缺少 input 时抛出异常")
    void should_throwException_when_hashTypeWithoutInput() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            exportService.exportResult(AlgorithmType.HASH, null);
        });
    }

    @Test
    @DisplayName("exportResult - SORT 类型缺少 input 时抛出异常")
    void should_throwException_when_sortTypeWithoutInput() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            exportService.exportResult(AlgorithmType.SORT, null);
        });
    }

    @Test
    @DisplayName("exportResult - SORT 类型 input 格式错误时抛出异常")
    void should_throwException_when_sortTypeWithInvalidInput() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            exportService.exportResult(AlgorithmType.SORT, "abc,def");
        });
    }

    // ==================== getFileName() 测试 ====================

    @Test
    @DisplayName("getFileName - HELLO 类型返回正确文件名")
    void should_returnCorrectFileName_when_typeIsHello() {
        // Act
        String fileName = exportService.getFileName(AlgorithmType.HELLO);

        // Assert
        assertNotNull(fileName);
        assertTrue(fileName.startsWith("hello_result_"));
        assertTrue(fileName.endsWith(".csv"));
    }

    @Test
    @DisplayName("getFileName - HASH 类型返回正确文件名")
    void should_returnCorrectFileName_when_typeIsHash() {
        // Act
        String fileName = exportService.getFileName(AlgorithmType.HASH);

        // Assert
        assertNotNull(fileName);
        assertTrue(fileName.startsWith("hash_result_"));
        assertTrue(fileName.endsWith(".csv"));
    }

    @Test
    @DisplayName("getFileName - SORT 类型返回正确文件名")
    void should_returnCorrectFileName_when_typeIsSort() {
        // Act
        String fileName = exportService.getFileName(AlgorithmType.SORT);

        // Assert
        assertNotNull(fileName);
        assertTrue(fileName.startsWith("sort_result_"));
        assertTrue(fileName.endsWith(".csv"));
    }
}

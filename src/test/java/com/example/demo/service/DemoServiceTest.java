package com.example.demo.service;

import com.example.demo.common.exception.DemoException;
import com.example.demo.service.impl.DemoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DemoService 单元测试。
 */
@DisplayName("DemoService 单元测试")
class DemoServiceTest {

    private DemoService demoService;

    @BeforeEach
    void setUp() {
        demoService = new DemoServiceImpl();
    }

    @Nested
    @DisplayName("hello 方法")
    class HelloTests {

        @Test
        @DisplayName("正常返回问候语")
        void shouldReturnGreeting_whenCalled() {
            String result = demoService.hello();
            assertEquals("Hello, World!", result);
        }
    }

    @Nested
    @DisplayName("hash 方法")
    class HashTests {

        @Test
        @DisplayName("正常计算 SHA-256 哈希")
        void shouldReturnSha256Hash_whenValidInput() {
            String result = demoService.hash("hello world", "SHA-256");
            assertNotNull(result);
            assertEquals(64, result.length());
            assertEquals("b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9", result);
        }

        @Test
        @DisplayName("正常计算 MD5 哈希")
        void shouldReturnMd5Hash_whenValidInput() {
            String result = demoService.hash("hello world", "MD5");
            assertNotNull(result);
            assertEquals(32, result.length());
            assertEquals("5eb63bbbe01eeed093cb22bb8f5acdc3", result);
        }

        @Test
        @DisplayName("默认算法为 SHA-256")
        void shouldUseSha256ByDefault_whenAlgorithmNotSpecified() {
            String result = demoService.hash("hello world", null);
            assertNotNull(result);
            assertEquals(64, result.length());
        }

        @Test
        @DisplayName("输入为空时抛出异常")
        void shouldThrowException_whenInputIsNull() {
            DemoException ex = assertThrows(DemoException.class,
                    () -> demoService.hash(null, "SHA-256"));
            assertEquals("DEMO_001", ex.getErrorCode());
        }

        @Test
        @DisplayName("输入为空字符串时抛出异常")
        void shouldThrowException_whenInputIsEmpty() {
            DemoException ex = assertThrows(DemoException.class,
                    () -> demoService.hash("", "SHA-256"));
            assertEquals("DEMO_001", ex.getErrorCode());
        }

        @Test
        @DisplayName("不支持的算法时抛出异常")
        void shouldThrowException_whenAlgorithmNotSupported() {
            DemoException ex = assertThrows(DemoException.class,
                    () -> demoService.hash("test", "SHA-512"));
            assertEquals("DEMO_002", ex.getErrorCode());
        }

        @Test
        @DisplayName("输入过长时抛出异常")
        void shouldThrowException_whenInputTooLong() {
            String longInput = "a".repeat(10001);
            DemoException ex = assertThrows(DemoException.class,
                    () -> demoService.hash(longInput, "SHA-256"));
            assertEquals("DEMO_001", ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("bubbleSort 方法")
    class BubbleSortTests {

        @Test
        @DisplayName("正常排序")
        void shouldReturnSortedArray_whenValidInput() {
            int[] input = {5, 3, 8, 1, 2};
            int[] result = demoService.bubbleSort(input);
            assertArrayEquals(new int[]{1, 2, 3, 5, 8}, result);
        }

        @Test
        @DisplayName("空数组时抛出异常")
        void shouldThrowException_whenArrayIsEmpty() {
            DemoException ex = assertThrows(DemoException.class,
                    () -> demoService.bubbleSort(new int[]{}));
            assertEquals("DEMO_001", ex.getErrorCode());
        }

        @Test
        @DisplayName("数组为 null 时抛出异常")
        void shouldThrowException_whenArrayIsNull() {
            DemoException ex = assertThrows(DemoException.class,
                    () -> demoService.bubbleSort(null));
            assertEquals("DEMO_001", ex.getErrorCode());
        }

        @Test
        @DisplayName("已排序数组保持不变")
        void shouldKeepSortedArrayUnchanged() {
            int[] input = {1, 2, 3, 4, 5};
            int[] result = demoService.bubbleSort(input);
            assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result);
        }

        @Test
        @DisplayName("单元素数组正常返回")
        void shouldReturnSingleElementArray() {
            int[] input = {42};
            int[] result = demoService.bubbleSort(input);
            assertArrayEquals(new int[]{42}, result);
        }
    }

    @Nested
    @DisplayName("export 方法")
    class ExportTests {

        @Test
        @DisplayName("正常导出 JSON 格式")
        void shouldExportJson_whenValidRequest() {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "Hello, World!");
            byte[] result = demoService.export("hello", "json", data);
            assertNotNull(result);
            String content = new String(result);
            assertTrue(content.contains("hello"));
            assertTrue(content.contains("Hello, World!"));
        }

        @Test
        @DisplayName("正常导出 CSV 格式")
        void shouldExportCsv_whenValidRequest() {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "Hello, World!");
            byte[] result = demoService.export("hello", "csv", data);
            assertNotNull(result);
            String content = new String(result);
            assertTrue(content.contains("message"));
            assertTrue(content.contains("Hello, World!"));
        }

        @Test
        @DisplayName("type 为空时抛出异常")
        void shouldThrowException_whenTypeIsNull() {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "test");
            DemoException ex = assertThrows(DemoException.class,
                    () -> demoService.export(null, "json", data));
            assertEquals("DEMO_001", ex.getErrorCode());
        }

        @Test
        @DisplayName("无效 type 时抛出异常")
        void shouldThrowException_whenTypeInvalid() {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "test");
            DemoException ex = assertThrows(DemoException.class,
                    () -> demoService.export("invalid", "json", data));
            assertEquals("DEMO_001", ex.getErrorCode());
        }

        @Test
        @DisplayName("data 为空时抛出异常")
        void shouldThrowException_whenDataIsEmpty() {
            DemoException ex = assertThrows(DemoException.class,
                    () -> demoService.export("hello", "json", new HashMap<>()));
            assertEquals("DEMO_001", ex.getErrorCode());
        }

        @Test
        @DisplayName("不支持的格式时抛出异常")
        void shouldThrowException_whenFormatNotSupported() {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "test");
            DemoException ex = assertThrows(DemoException.class,
                    () -> demoService.export("hello", "xml", data));
            assertEquals("DEMO_004", ex.getErrorCode());
        }
    }
}
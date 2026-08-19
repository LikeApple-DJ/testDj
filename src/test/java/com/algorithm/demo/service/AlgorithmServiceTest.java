package com.algorithm.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AlgorithmService 单元测试
 * 覆盖场景：正常路径、参数校验、异常处理、边界值
 */
@DisplayName("AlgorithmService 单元测试")
class AlgorithmServiceTest {

    private AlgorithmService algorithmService;

    @BeforeEach
    void setUp() {
        algorithmService = new AlgorithmServiceImpl();
    }

    // ==================== hello() 测试 ====================

    @Test
    @DisplayName("hello - 正常返回 Hello World 消息")
    void should_returnHelloWorldMessage_when_callHello() {
        // Act
        String result = algorithmService.hello();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Hello World"));
    }

    @Test
    @DisplayName("hello - 返回结果包含时间戳")
    void should_returnTimestamp_when_callHello() {
        // Act
        String result = algorithmService.hello();

        // Assert
        assertNotNull(result);
        // 时间戳格式应包含日期时间信息
        assertTrue(result.length() > "Hello World".length());
    }

    // ==================== hash() 测试 ====================

    @Test
    @DisplayName("hash - 正常计算 SHA-256 哈希值")
    void should_returnHashValue_when_validInput() {
        // Arrange
        String input = "hello world";

        // Act
        String result = algorithmService.hash(input);

        // Assert
        assertNotNull(result);
        assertEquals(64, result.length()); // SHA-256 输出为 64 位十六进制
        assertTrue(result.matches("[0-9a-f]+")); // 十六进制格式
    }

    @Test
    @DisplayName("hash - 相同输入返回相同哈希值")
    void should_returnSameHash_when_sameInput() {
        // Arrange
        String input = "test";

        // Act
        String result1 = algorithmService.hash(input);
        String result2 = algorithmService.hash(input);

        // Assert
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("hash - 不同输入返回不同哈希值")
    void should_returnDifferentHash_when_differentInput() {
        // Act
        String result1 = algorithmService.hash("hello");
        String result2 = algorithmService.hash("world");

        // Assert
        assertNotEquals(result1, result2);
    }

    @Test
    @DisplayName("hash - 输入为空时抛出异常")
    void should_throwException_when_inputIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            algorithmService.hash(null);
        });
    }

    @Test
    @DisplayName("hash - 输入为空字符串时抛出异常")
    void should_throwException_when_inputIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            algorithmService.hash("");
        });
    }

    @Test
    @DisplayName("hash - 输入为空白字符串时抛出异常")
    void should_throwException_when_inputIsBlank() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            algorithmService.hash("   ");
        });
    }

    // ==================== bubbleSort() 测试 ====================

    @Test
    @DisplayName("bubbleSort - 正常排序无序列表")
    void should_returnSortedList_when_validInput() {
        // Arrange
        List<Integer> numbers = Arrays.asList(5, 3, 8, 1, 2);

        // Act
        List<Integer> result = algorithmService.bubbleSort(numbers);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 5, 8), result);
    }

    @Test
    @DisplayName("bubbleSort - 已排序列表保持不变")
    void should_returnSameList_when_alreadySorted() {
        // Arrange
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        // Act
        List<Integer> result = algorithmService.bubbleSort(numbers);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result);
    }

    @Test
    @DisplayName("bubbleSort - 逆序列表正确排序")
    void should_returnSortedList_when_reverseOrder() {
        // Arrange
        List<Integer> numbers = Arrays.asList(5, 4, 3, 2, 1);

        // Act
        List<Integer> result = algorithmService.bubbleSort(numbers);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result);
    }

    @Test
    @DisplayName("bubbleSort - 单元素列表返回相同列表")
    void should_returnSameList_when_singleElement() {
        // Arrange
        List<Integer> numbers = Collections.singletonList(42);

        // Act
        List<Integer> result = algorithmService.bubbleSort(numbers);

        // Assert
        assertEquals(Collections.singletonList(42), result);
    }

    @Test
    @DisplayName("bubbleSort - 包含重复元素正确排序")
    void should_returnSortedList_when_duplicateElements() {
        // Arrange
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5);

        // Act
        List<Integer> result = algorithmService.bubbleSort(numbers);

        // Assert
        assertEquals(Arrays.asList(1, 1, 2, 3, 4, 5, 5, 6, 9), result);
    }

    @Test
    @DisplayName("bubbleSort - 包含负数正确排序")
    void should_returnSortedList_when_negativeNumbers() {
        // Arrange
        List<Integer> numbers = Arrays.asList(3, -1, 4, -5, 2);

        // Act
        List<Integer> result = algorithmService.bubbleSort(numbers);

        // Assert
        assertEquals(Arrays.asList(-5, -1, 2, 3, 4), result);
    }

    @Test
    @DisplayName("bubbleSort - 输入为空列表时抛出异常")
    void should_throwException_when_listIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            algorithmService.bubbleSort(Collections.emptyList());
        });
    }

    @Test
    @DisplayName("bubbleSort - 输入为 null 时抛出异常")
    void should_throwException_when_listIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            algorithmService.bubbleSort(null);
        });
    }

    @Test
    @DisplayName("bubbleSort - 不修改原始输入列表")
    void should_notModifyOriginalList_when_sort() {
        // Arrange
        List<Integer> original = Arrays.asList(5, 3, 8, 1, 2);
        List<Integer> expected = Arrays.asList(5, 3, 8, 1, 2);

        // Act
        algorithmService.bubbleSort(original);

        // Assert
        assertEquals(expected, original);
    }
}

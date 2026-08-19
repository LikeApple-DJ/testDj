package com.dtcode.demo.demo.service.impl;

import com.dtcode.demo.common.exception.BusinessException;
import com.dtcode.demo.demo.model.dto.BubbleSortDTO;
import com.dtcode.demo.demo.model.dto.HashDTO;
import com.dtcode.demo.demo.model.dto.HelloWorldDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DemoServiceImpl 单元测试
 *
 * @author DTCoder
 */
class DemoServiceImplTest {

    private DemoServiceImpl demoService;

    @BeforeEach
    void setUp() {
        demoService = new DemoServiceImpl();
        demoService.setCallerContext("test-user");
    }

    @AfterEach
    void tearDown() {
        demoService.clearCallerContext();
    }

    // ========== HelloWorld 测试 ==========

    @Test
    @DisplayName("helloWorld - 正常路径：传入名称返回问候语")
    void should_returnGreeting_when_validNameProvided() {
        HelloWorldDTO result = demoService.helloWorld("张三");
        assertNotNull(result);
        assertEquals("Hello, 张三!", result.getResult());
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("helloWorld - 默认值路径：name为null时使用World")
    void should_returnDefaultGreeting_when_nameIsNull() {
        HelloWorldDTO result = demoService.helloWorld(null);
        assertNotNull(result);
        assertEquals("Hello, World!", result.getResult());
    }

    @Test
    @DisplayName("helloWorld - 默认值路径：name为空字符串时使用World")
    void should_returnDefaultGreeting_when_nameIsEmpty() {
        HelloWorldDTO result = demoService.helloWorld("");
        assertNotNull(result);
        assertEquals("Hello, World!", result.getResult());
    }

    @Test
    @DisplayName("helloWorld - 缓存验证：执行后结果被缓存")
    void should_cacheResult_when_helloWorldExecuted() {
        demoService.helloWorld("Test");
        Object cached = demoService.getCachedResult(DemoServiceImpl.CACHE_KEY_HELLOWORLD);
        assertNotNull(cached);
        assertTrue(cached instanceof HelloWorldDTO);
    }

    // ========== Hash 测试 ==========

    @Test
    @DisplayName("hash - 正常路径：返回正确的SHA-256哈希值")
    void should_returnHashValue_when_validInputProvided() {
        HashDTO result = demoService.hash("hello world");
        assertNotNull(result);
        assertEquals("hello world", result.getInput());
        assertEquals("SHA-256", result.getAlgorithm());
        assertEquals("b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9", result.getHashValue());
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("hash - 参数校验：input为null时抛出BusinessException")
    void should_throwException_when_inputIsNull() {
        BusinessException ex = assertThrows(BusinessException.class, () -> demoService.hash(null));
        assertEquals("DEMO_002", ex.getErrorCode());
    }

    @Test
    @DisplayName("hash - 参数校验：input为空字符串时抛出BusinessException")
    void should_throwException_when_inputIsEmpty() {
        BusinessException ex = assertThrows(BusinessException.class, () -> demoService.hash(""));
        assertEquals("DEMO_002", ex.getErrorCode());
    }

    @Test
    @DisplayName("hash - 缓存验证：执行后结果被缓存")
    void should_cacheResult_when_hashExecuted() {
        demoService.hash("test");
        Object cached = demoService.getCachedResult(DemoServiceImpl.CACHE_KEY_HASH);
        assertNotNull(cached);
        assertTrue(cached instanceof HashDTO);
    }

    // ========== BubbleSort 测试 ==========

    @Test
    @DisplayName("bubbleSort - 正常路径：返回升序排列结果")
    void should_returnSortedArray_when_validInputProvided() {
        List<Integer> input = Arrays.asList(5, 3, 8, 1, 9, 2);
        BubbleSortDTO result = demoService.bubbleSort(input);
        assertNotNull(result);
        assertEquals(Arrays.asList(5, 3, 8, 1, 9, 2), result.getOriginal());
        assertEquals(Arrays.asList(1, 2, 3, 5, 8, 9), result.getSorted());
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("bubbleSort - 边界值：单元素数组正常返回")
    void should_returnSameArray_when_singleElement() {
        List<Integer> input = Collections.singletonList(42);
        BubbleSortDTO result = demoService.bubbleSort(input);
        assertNotNull(result);
        assertEquals(Collections.singletonList(42), result.getSorted());
    }

    @Test
    @DisplayName("bubbleSort - 边界值：已排序数组正常返回")
    void should_returnSameArray_when_alreadySorted() {
        List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        BubbleSortDTO result = demoService.bubbleSort(input);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result.getSorted());
    }

    @Test
    @DisplayName("bubbleSort - 参数校验：numbers为null时抛出BusinessException")
    void should_throwException_when_numbersIsNull() {
        BusinessException ex = assertThrows(BusinessException.class, () -> demoService.bubbleSort(null));
        assertEquals("DEMO_004", ex.getErrorCode());
    }

    @Test
    @DisplayName("bubbleSort - 参数校验：空列表时抛出BusinessException")
    void should_throwException_when_numbersIsEmpty() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> demoService.bubbleSort(Collections.emptyList()));
        assertEquals("DEMO_004", ex.getErrorCode());
    }

    @Test
    @DisplayName("bubbleSort - 原始数组不被修改")
    void should_notModifyOriginalArray_when_sorting() {
        List<Integer> input = Arrays.asList(5, 3, 1);
        BubbleSortDTO result = demoService.bubbleSort(input);
        assertEquals(Arrays.asList(5, 3, 1), result.getOriginal());
        assertEquals(Arrays.asList(1, 3, 5), result.getSorted());
    }
}

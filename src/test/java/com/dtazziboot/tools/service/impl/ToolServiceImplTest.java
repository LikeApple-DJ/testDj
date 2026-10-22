package com.dtazziboot.tools.service.impl;

import com.dtazziboot.tools.common.exception.BizException;
import com.dtazziboot.tools.model.dto.BubbleSortResult;
import com.dtazziboot.tools.model.dto.HashResult;
import com.dtazziboot.tools.model.dto.HelloRequest;
import com.dtazziboot.tools.model.dto.HelloResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link ToolServiceImpl} 单元测试。
 */
class ToolServiceImplTest {

    private ToolServiceImpl toolService;

    @BeforeEach
    void setUp() {
        toolService = new ToolServiceImpl();
    }

    // ==================== hello 测试 ====================

    @Test
    void should_greetByName_when_nameProvided() {
        HelloRequest request = new HelloRequest();
        request.setName("Alice");

        HelloResult result = toolService.hello(request);

        assertThat(result.getMessage()).isEqualTo("Hello, Alice");
    }

    @Test
    void should_defaultToWorld_when_nameBlank() {
        HelloRequest request = new HelloRequest();
        request.setName("  ");

        HelloResult result = toolService.hello(request);

        assertThat(result.getMessage()).isEqualTo("Hello, World");
    }

    // ==================== hash 测试 ====================

    @Test
    void should_returnMd5_when_algorithmMd5() {
        HashResult result = toolService.hash("abc", "MD5");

        assertThat(result.getAlgorithm()).isEqualTo("MD5");
        assertThat(result.getHash()).isEqualTo("900150983cd24fb0d6963f7d28e17f72");
    }

    @Test
    void should_returnSha256_when_algorithmSha256() {
        HashResult result = toolService.hash("abc", "SHA256");

        assertThat(result.getAlgorithm()).isEqualTo("SHA256");
        assertThat(result.getHash())
                .isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
    }

    @Test
    void should_throwException_when_algorithmUnsupported() {
        assertThatThrownBy(() -> toolService.hash("abc", "SHA1"))
                .isInstanceOf(BizException.class);
    }

    @Test
    void should_throwException_when_textEmpty() {
        assertThatThrownBy(() -> toolService.hash("", "MD5"))
                .isInstanceOf(BizException.class);
    }

    // ==================== bubbleSort 测试 ====================

    @Test
    void should_sortAscending_when_orderDefault() {
        int[] nums = {5, 2, 9, 1, 5, 6};

        BubbleSortResult result = toolService.bubbleSort(nums, null);

        assertThat(result.getOrder()).isEqualTo("ASC");
        assertThat(result.getSorted()).containsExactly(1, 2, 5, 5, 6, 9);
        assertThat(result.getOriginal()).containsExactly(5, 2, 9, 1, 5, 6);
    }

    @Test
    void should_sortDescending_when_orderDesc() {
        int[] nums = {3, 8, 1};

        BubbleSortResult result = toolService.bubbleSort(nums, "DESC");

        assertThat(result.getSorted()).containsExactly(8, 3, 1);
    }

    @Test
    void should_throwException_when_orderUnsupported() {
        assertThatThrownBy(() -> toolService.bubbleSort(new int[] {1, 2}, "ASCD"))
                .isInstanceOf(BizException.class);
    }

    @Test
    void should_returnEmpty_when_numsEmpty() {
        BubbleSortResult result = toolService.bubbleSort(new int[] {}, "ASC");

        assertThat(result.getSorted()).isEmpty();
        assertThat(result.getSwapCount()).isZero();
    }
}
package com.example.algorithm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link BubbleSortUtil} 单元测试.
 */
class BubbleSortUtilTest {

    // ==================== sort(int[]) 正常路径 ====================

    @Test
    @DisplayName("正常排序：无序数组升序排列")
    void should_returnSortedArray_when_givenUnsortedArray() {
        // Arrange
        int[] input = {5, 3, 8, 1, 2};

        // Act
        BubbleSortUtil.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 2, 3, 5, 8);
    }

    @Test
    @DisplayName("已排序数组保持不变")
    void should_remainUnchanged_when_alreadySorted() {
        // Arrange
        int[] input = {1, 2, 3, 4, 5};

        // Act
        BubbleSortUtil.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    @DisplayName("逆序数组正确排序")
    void should_sortCorrectly_when_givenReverseOrderArray() {
        // Arrange
        int[] input = {5, 4, 3, 2, 1};

        // Act
        BubbleSortUtil.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 2, 3, 4, 5);
    }

    // ==================== sort(int[]) 边界值 ====================

    @Test
    @DisplayName("空数组不抛异常")
    void should_notThrow_when_givenEmptyArray() {
        // Arrange
        int[] input = {};

        // Act
        BubbleSortUtil.sort(input);

        // Assert
        assertThat(input).isEmpty();
    }

    @Test
    @DisplayName("null 输入抛出 IllegalArgumentException")
    void should_throwException_when_givenNullArray() {
        assertThatThrownBy(() -> BubbleSortUtil.sort(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }

    @Test
    @DisplayName("单元素数组保持不变")
    void should_remainUnchanged_when_givenSingleElement() {
        // Arrange
        int[] input = {42};

        // Act
        BubbleSortUtil.sort(input);

        // Assert
        assertThat(input).containsExactly(42);
    }

    @Test
    @DisplayName("含重复元素的数组正确排序")
    void should_sortCorrectly_when_givenDuplicateElements() {
        // Arrange
        int[] input = {3, 1, 2, 3, 1};

        // Act
        BubbleSortUtil.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 1, 2, 3, 3);
    }

    @Test
    @DisplayName("含负数的数组正确排序")
    void should_sortCorrectly_when_givenNegativeNumbers() {
        // Arrange
        int[] input = {-3, 0, -1, 5, 2};

        // Act
        BubbleSortUtil.sort(input);

        // Assert
        assertThat(input).containsExactly(-3, -1, 0, 2, 5);
    }

    @Test
    @DisplayName("两个元素的数组正确排序")
    void should_sortCorrectly_when_givenTwoElements() {
        // Arrange
        int[] input = {9, 1};

        // Act
        BubbleSortUtil.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 9);
    }

    // ==================== sort(int[]) 原址排序验证 ====================

    @Test
    @DisplayName("排序在原数组上进行，不产生新数组")
    void should_sortInPlace_when_givenArray() {
        // Arrange
        int[] input = {3, 1, 2};
        int[] reference = input;

        // Act
        BubbleSortUtil.sort(input);

        // Assert
        assertThat(input).isSameAs(reference);
    }
}

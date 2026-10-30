package com.example.sort;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * {@link BubbleSort} 单元测试。
 *
 * <p>覆盖场景：正常路径、边界值（空数组/单元素/已有序/逆序/重复元素）、异常路径（null 入参）。
 */
class BubbleSortTest {

    // ==================== sort(int[]) 测试 ====================

    @Test
    void should_sortArray_when_unsorted() {
        // Arrange
        int[] array = {5, 3, 8, 1, 2};

        // Act
        BubbleSort.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 2, 3, 5, 8);
    }

    @Test
    void should_returnSameArray_when_alreadySorted() {
        // Arrange
        int[] array = {1, 2, 3, 4, 5};

        // Act
        BubbleSort.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void should_returnSameArray_when_singleElement() {
        // Arrange
        int[] array = {42};

        // Act
        BubbleSort.sort(array);

        // Assert
        assertThat(array).containsExactly(42);
    }

    @Test
    void should_returnEmptyArray_when_emptyArray() {
        // Arrange
        int[] array = {};

        // Act
        BubbleSort.sort(array);

        // Assert
        assertThat(array).isEmpty();
    }

    @Test
    void should_sortArray_when_reverseSorted() {
        // Arrange
        int[] array = {9, 7, 5, 3, 1};

        // Act
        BubbleSort.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 3, 5, 7, 9);
    }

    @Test
    void should_sortArray_when_containsDuplicates() {
        // Arrange
        int[] array = {4, 2, 4, 1, 2};

        // Act
        BubbleSort.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 2, 2, 4, 4);
    }

    @Test
    void should_throwNullPointerException_when_intArrayIsNull() {
        // Act & Assert
        assertThatNullPointerException()
                .isThrownBy(() -> BubbleSort.sort((int[]) null));
    }

    // ==================== sort(T[], Comparator) 测试 ====================

    @Test
    void should_sortIntegerArray_when_unsorted() {
        // Arrange
        Integer[] array = {5, 3, 8, 1, 2};

        // Act
        BubbleSort.sort(array, Comparator.naturalOrder());

        // Assert
        assertThat(array).containsExactly(1, 2, 3, 5, 8);
    }

    @Test
    void should_sortStringArray_when_unsorted() {
        // Arrange
        String[] array = {"delta", "alpha", "charlie", "bravo"};

        // Act
        BubbleSort.sort(array, Comparator.naturalOrder());

        // Assert
        assertThat(array).containsExactly("alpha", "bravo", "charlie", "delta");
    }

    @Test
    void should_sortDescending_when_reverseComparator() {
        // Arrange
        Integer[] array = {1, 3, 2, 5, 4};

        // Act
        BubbleSort.sort(array, Comparator.reverseOrder());

        // Assert
        assertThat(array).containsExactly(5, 4, 3, 2, 1);
    }

    @Test
    void should_throwNullPointerException_when_genericArrayIsNull() {
        // Act & Assert
        assertThatNullPointerException()
                .isThrownBy(() -> BubbleSort.sort((Integer[]) null, Comparator.naturalOrder()));
    }

    @Test
    void should_throwNullPointerException_when_comparatorIsNull() {
        // Arrange
        Integer[] array = {3, 1, 2};

        // Act & Assert
        assertThatNullPointerException()
                .isThrownBy(() -> BubbleSort.sort(array, null));
    }
}
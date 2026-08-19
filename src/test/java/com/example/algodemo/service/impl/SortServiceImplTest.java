package com.example.algodemo.service.impl;

import com.example.algodemo.common.exception.BusinessException;
import com.example.algodemo.service.model.SortResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SortServiceImplTest {

    private final SortServiceImpl sortService = new SortServiceImpl();

    @Test
    void should_returnAscendingSortedArray_when_orderIsAsc() {
        // Arrange
        int[] input = {3, 1, 4, 1, 5, 9, 2, 6};

        // Act
        SortResult result = sortService.bubbleSort(input, "ASC");

        // Assert
        assertThat(result.getOrder()).isEqualTo("ASC");
        assertThat(result.getOriginalArray()).containsExactly(3, 1, 4, 1, 5, 9, 2, 6);
        assertThat(result.getSortedArray()).containsExactly(1, 1, 2, 3, 4, 5, 6, 9);
    }

    @Test
    void should_returnDescendingSortedArray_when_orderIsDesc() {
        // Arrange
        int[] input = {3, 1, 4, 1, 5, 9, 2, 6};

        // Act
        SortResult result = sortService.bubbleSort(input, "DESC");

        // Assert
        assertThat(result.getOrder()).isEqualTo("DESC");
        assertThat(result.getSortedArray()).containsExactly(9, 6, 5, 4, 3, 2, 1, 1);
    }

    @Test
    void should_defaultToAscending_when_orderIsNull() {
        // Arrange
        int[] input = {3, 1, 4};

        // Act
        SortResult result = sortService.bubbleSort(input, null);

        // Assert
        assertThat(result.getOrder()).isEqualTo("ASC");
        assertThat(result.getSortedArray()).containsExactly(1, 3, 4);
    }

    @Test
    void should_throwBusinessException_when_arrayIsEmpty() {
        // Act & Assert
        assertThatThrownBy(() -> sortService.bubbleSort(new int[]{}, "ASC"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo("ALG_003");
                });
    }

    @Test
    void should_throwBusinessException_when_orderIsInvalid() {
        // Act & Assert
        assertThatThrownBy(() -> sortService.bubbleSort(new int[]{1, 2}, "UP"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo("ALG_001");
                });
    }
}

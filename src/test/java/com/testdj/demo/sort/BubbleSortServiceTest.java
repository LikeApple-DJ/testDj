package com.testdj.demo.sort;

import com.testdj.demo.common.ErrorCode;
import com.testdj.demo.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BubbleSortServiceTest {

    private final BubbleSortService bubbleSortService = new BubbleSortService();

    @Test
    void shouldSortAscending() {
        SortRequest request = new SortRequest(List.of(3, 1, 4, 1, 5), true, false);
        SortResponse response = bubbleSortService.sort(request);
        assertEquals(List.of(3, 1, 4, 1, 5), response.input());
        assertEquals(List.of(1, 1, 3, 4, 5), response.output());
    }

    @Test
    void shouldSortDescending() {
        SortRequest request = new SortRequest(List.of(3, 1, 4, 1, 5), false, false);
        SortResponse response = bubbleSortService.sort(request);
        assertEquals(List.of(5, 4, 3, 1, 1), response.output());
    }

    @Test
    void shouldSortAndDeduplicate() {
        SortRequest request = new SortRequest(List.of(3, 1, 4, 1, 5), true, true);
        SortResponse response = bubbleSortService.sort(request);
        assertEquals(List.of(1, 3, 4, 5), response.output());
    }

    @Test
    void shouldThrowWhenNumbersIsNull() {
        SortRequest request = new SortRequest(null, true, false);
        BusinessException ex = assertThrows(BusinessException.class, () -> bubbleSortService.sort(request));
        assertEquals(ErrorCode.SORT_NUMBERS_EMPTY, ex.getCode());
        assertTrue(ex.getMessage().contains("numbers must not be empty"));
    }

    @Test
    void shouldThrowWhenNumbersIsEmpty() {
        SortRequest request = new SortRequest(List.of(), true, false);
        BusinessException ex = assertThrows(BusinessException.class, () -> bubbleSortService.sort(request));
        assertEquals(ErrorCode.SORT_NUMBERS_EMPTY, ex.getCode());
        assertTrue(ex.getMessage().contains("numbers must not be empty"));
    }
}

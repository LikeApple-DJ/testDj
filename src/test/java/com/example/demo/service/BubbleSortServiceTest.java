package com.example.demo.service;

import com.example.demo.dto.SortResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BubbleSortServiceTest {

    private final BubbleSortService service = new BubbleSortService();

    @Test
    void testBubbleSort() {
        SortResponse result = service.bubbleSort(List.of(5, 3, 8, 1, 9));
        assertEquals(List.of(5, 3, 8, 1, 9), result.getOriginal());
        assertEquals(List.of(1, 3, 5, 8, 9), result.getSorted());
        assertTrue(result.getSteps() > 0);
    }

    @Test
    void testAlreadySorted() {
        SortResponse result = service.bubbleSort(List.of(1, 2, 3));
        assertEquals(List.of(1, 2, 3), result.getSorted());
    }

    @Test
    void testEmptyArray() {
        SortResponse result = service.bubbleSort(List.of());
        assertEquals(List.of(), result.getSorted());
        assertEquals(0, result.getSteps());
    }
}

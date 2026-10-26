package com.dt.example.algorithm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BubbleSortService 测试")
class BubbleSortServiceTest {

    private final BubbleSortService service = new BubbleSortService();

    @Test
    @DisplayName("乱序数组应升序排序")
    void should_sortAscending() {
        assertThat(service.sort(new int[]{5, 3, 8, 1, 9}))
            .containsExactly(1, 3, 5, 8, 9);
    }

    @Test
    @DisplayName("已排序数组应保持不变")
    void should_keepSorted() {
        assertThat(service.sort(new int[]{1, 2, 3}))
            .containsExactly(1, 2, 3);
    }

    @Test
    @DisplayName("空数组应安全返回空数组")
    void should_handleEmpty() {
        assertThat(service.sort(new int[]{})).isEmpty();
    }

    @Test
    @DisplayName("不应修改入参数组")
    void should_notMutateInput() {
        int[] input = {3, 1, 2};
        service.sort(input);
        assertThat(input).containsExactly(3, 1, 2);
    }
}
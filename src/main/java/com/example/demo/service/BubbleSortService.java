package com.example.demo.service;

import com.example.demo.dto.BubbleSortResponse;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BubbleSortService {

    private static final int MAX_LENGTH = 100;

    public BubbleSortResponse sort(int[] array, String order) {
        if (!"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)) {
            throw new IllegalArgumentException("排序方向仅支持 asc 或 desc");
        }
        if (array.length > MAX_LENGTH) {
            throw new IllegalArgumentException("数组长度不能超过 " + MAX_LENGTH);
        }

        List<Integer> original = Arrays.stream(array).boxed().collect(Collectors.toList());
        int[] working = Arrays.copyOf(array, array.length);
        boolean ascending = "asc".equalsIgnoreCase(order);
        List<BubbleSortResponse.SortStep> steps = new ArrayList<>();
        int comparisons = 0;
        int n = working.length;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                comparisons++;
                boolean shouldSwap = ascending ? working[j] > working[j + 1]
                                               : working[j] < working[j + 1];
                if (shouldSwap) {
                    int tmp = working[j];
                    working[j] = working[j + 1];
                    working[j + 1] = tmp;
                    swapped = true;
                }
            }
            steps.add(new BubbleSortResponse.SortStep(
                    i + 1,
                    Arrays.stream(working).boxed().collect(Collectors.toList())));
            if (!swapped) break;
        }

        List<Integer> sorted = Arrays.stream(working).boxed().collect(Collectors.toList());
        return new BubbleSortResponse(original, sorted, steps, comparisons);
    }
}
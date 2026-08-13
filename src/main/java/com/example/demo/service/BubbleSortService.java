package com.example.demo.service;

import com.example.demo.dto.SortResponse;
import com.example.demo.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BubbleSortService {

    private static final int MAX_ARRAY_SIZE = 10000;

    public SortResponse bubbleSort(List<Integer> input) {
        if (input == null || input.isEmpty()) {
            throw new BusinessException("BIZ_004", "array 不能为空");
        }
        if (input.size() > MAX_ARRAY_SIZE) {
            throw new BusinessException("BIZ_004", "array 长度不超过 10000");
        }
        List<Integer> arr = new ArrayList<>(input);
        int n = arr.size();
        int steps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                steps++;
                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }

        return new SortResponse(input, arr, steps);
    }
}

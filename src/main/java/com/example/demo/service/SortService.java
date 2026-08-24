package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SortService {

    public record SortResult(List<Integer> original, List<Integer> sorted, int swapCount) {
    }

    public SortResult bubbleSort(List<Integer> numbers, String order) {
        if (numbers == null || numbers.isEmpty()) {
            return new SortResult(List.of(), List.of(), 0);
        }

        String normalizedOrder = (order != null) ? order.toLowerCase() : "asc";
        List<Integer> working = new ArrayList<>(numbers);
        int swapCount = 0;
        int n = working.size();

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                boolean needSwap;
                if ("desc".equals(normalizedOrder)) {
                    needSwap = working.get(j) < working.get(j + 1);
                } else {
                    needSwap = working.get(j) > working.get(j + 1);
                }
                if (needSwap) {
                    int temp = working.get(j);
                    working.set(j, working.get(j + 1));
                    working.set(j + 1, temp);
                    swapCount++;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }

        return new SortResult(new ArrayList<>(numbers), working, swapCount);
    }
}
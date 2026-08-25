package com.testdj.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BubbleSortService {

    public Map<String, Object> sort(List<Integer> array) {
        List<Integer> original = new ArrayList<>(array);
        List<Integer> sorted = new ArrayList<>(array);
        int steps = bubbleSort(sorted);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("original", original);
        result.put("sorted", sorted);
        result.put("steps", steps);
        return result;
    }

    private int bubbleSort(List<Integer> arr) {
        int n = arr.size();
        int steps = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
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
        return steps;
    }
}
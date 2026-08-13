package com.example.demo.service;

import com.example.demo.dto.SortResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BubbleSortService {

    public SortResponse bubbleSort(List<Integer> input) {
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

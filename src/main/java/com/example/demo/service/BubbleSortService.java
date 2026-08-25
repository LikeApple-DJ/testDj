package com.example.demo.service;
import org.springframework.stereotype.Service;
import java.util.Arrays;
@Service
public class BubbleSortService {
    public int[] sort(int[] array) {
        int[] result = Arrays.copyOf(array, array.length);
        int n = result.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (result[j] > result[j + 1]) {
                    int temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                }
            }
        }
        return result;
    }
}
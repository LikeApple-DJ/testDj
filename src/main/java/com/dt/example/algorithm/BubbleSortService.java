package com.dt.example.algorithm;

import org.springframework.stereotype.Service;

/**
 * 冒泡排序服务。
 */
@Service
public class BubbleSortService {

    /**
     * 返回升序排序后的新数组，不修改入参。
     *
     * @param numbers 整数数组
     * @return 升序新数组
     */
    public int[] sort(int[] numbers) {
        int[] result = numbers.clone();
        for (int i = 0; i < result.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < result.length - 1 - i; j++) {
                if (result[j] > result[j + 1]) {
                    int tmp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return result;
    }
}
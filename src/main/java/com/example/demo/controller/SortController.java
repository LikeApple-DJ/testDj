package com.example.demo.controller;

import com.example.demo.dto.SortRequest;
import com.example.demo.dto.SortResponse;
import com.example.demo.dto.SortStep;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/sort")
public class SortController {

    // 复用 Random 实例，避免每次请求创建新对象
    private static final Random RAND = new Random();

    @PostMapping("/bubble")
    public SortResponse bubbleSort(@RequestBody(required = false) SortRequest request) {
        // 默认参数
        int size = (request != null && request.getArraySize() != null) ? request.getArraySize() : 10;
        int min = (request != null && request.getMin() != null) ? request.getMin() : 1;
        int max = (request != null && request.getMax() != null) ? request.getMax() : 100;

        // 生成随机数组
        Random rand = RAND;
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(max - min + 1) + min;
        }

        int[] original = arr.clone();
        List<SortStep> steps = new ArrayList<>();
        int swapCount = 0;
        int n = arr.length;

        // 冒泡排序
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapCount++;
                }
            }
            steps.add(new SortStep(i + 1, arr.clone()));
        }

        SortResponse response = new SortResponse();
        response.setOriginalArray(original);
        response.setSortedArray(arr);
        response.setSteps(steps);
        response.setTotalRounds(n - 1);
        response.setSwapCount(swapCount);
        return response;
    }
}
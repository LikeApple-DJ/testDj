package com.algorithm.demo.controller;

import com.algorithm.demo.common.Result;
import com.algorithm.demo.model.dto.*;
import com.algorithm.demo.service.AlgorithmService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 算法控制器
 *
 * @author DTCoder
 */
@RestController
@RequestMapping("/api/algorithm")
public class AlgorithmController {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmController.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    /**
     * W01 HelloWorld 执行
     */
    @GetMapping("/hello")
    public Result<HelloResponse> hello() {
        long startTime = System.currentTimeMillis();
        String message = algorithmService.hello();
        long cost = System.currentTimeMillis() - startTime;
        log.info("接口 /api/algorithm/hello 调用, cost={}ms", cost);

        HelloResponse response = new HelloResponse(
                "Hello World",
                LocalDateTime.now().format(FORMATTER)
        );
        return Result.success(response);
    }

    /**
     * W02 哈希算法执行
     */
    @PostMapping("/hash")
    public Result<HashResponse> hash(@Valid @RequestBody HashRequest request) {
        long startTime = System.currentTimeMillis();
        String hashValue = algorithmService.hash(request.getInput());
        long cost = System.currentTimeMillis() - startTime;
        log.info("接口 /api/algorithm/hash 调用, inputLength={}, cost={}ms",
                request.getInput().length(), cost);

        HashResponse response = new HashResponse(
                request.getInput(),
                "SHA-256",
                hashValue
        );
        return Result.success(response);
    }

    /**
     * W03 冒泡排序执行
     */
    @PostMapping("/sort")
    public Result<SortResponse> sort(@Valid @RequestBody SortRequest request) {
        long startTime = System.currentTimeMillis();
        List<Integer> sorted = algorithmService.bubbleSort(request.getNumbers());
        long cost = System.currentTimeMillis() - startTime;
        log.info("接口 /api/algorithm/sort 调用, size={}, cost={}ms",
                request.getNumbers().size(), cost);

        // 计算交换次数（重新执行一次以统计）
        int swapCount = countSwaps(request.getNumbers());

        SortResponse response = new SortResponse(
                request.getNumbers(),
                sorted,
                swapCount
        );
        return Result.success(response);
    }

    /**
     * 统计冒泡排序交换次数
     */
    private int countSwaps(List<Integer> numbers) {
        List<Integer> list = new java.util.ArrayList<>(numbers);
        int n = list.size();
        int swapCount = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (list.get(j) > list.get(j + 1)) {
                    int temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swapCount++;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return swapCount;
    }
}

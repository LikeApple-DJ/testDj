package com.algorithm.demo.controller;

import com.algorithm.demo.common.Result;
import com.algorithm.demo.model.dto.HashRequest;
import com.algorithm.demo.model.dto.HashResponse;
import com.algorithm.demo.model.dto.HelloResponse;
import com.algorithm.demo.model.dto.SortRequest;
import com.algorithm.demo.model.dto.SortResponse;
import com.algorithm.demo.model.dto.SortResult;
import com.algorithm.demo.service.AlgorithmService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                message,
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
        SortResult sortResult = algorithmService.bubbleSort(request.getNumbers());
        long cost = System.currentTimeMillis() - startTime;
        log.info("接口 /api/algorithm/sort 调用, size={}, cost={}ms",
                request.getNumbers().size(), cost);

        SortResponse response = new SortResponse(
                request.getNumbers(),
                sortResult.getSorted(),
                sortResult.getSwapCount()
        );
        return Result.success(response);
    }
}

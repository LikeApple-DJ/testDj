package com.dt.example.web;

import com.dt.example.algorithm.BubbleSortService;
import com.dt.example.algorithm.HashService;
import com.dt.example.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 三个演示业务接口。
 */
@RestController
@RequestMapping("/api")
public class BusinessController {

    private final HashService hashService;
    private final BubbleSortService bubbleSortService;

    public BusinessController(HashService hashService, BubbleSortService bubbleSortService) {
        this.hashService = hashService;
        this.bubbleSortService = bubbleSortService;
    }

    @GetMapping("/helloWorld")
    public Result<String> helloWorld() {
        return Result.ok("Hello, World!");
    }

    @PostMapping("/hash")
    public Result<Map<String, Object>> hash(@RequestBody HashRequest request) {
        if (request.text() == null) {
            throw new IllegalArgumentException("text 不能为空");
        }
        String hash = hashService.md5Hex(request.text());
        return Result.ok(Map.of(
            "algorithm", "MD5",
            "input", request.text(),
            "hash", hash
        ));
    }

    @PostMapping("/sort/bubble")
    public Result<Map<String, Object>> bubbleSort(@RequestBody SortRequest request) {
        if (request.numbers() == null) {
            throw new IllegalArgumentException("numbers 不能为空");
        }
        int[] sorted = bubbleSortService.sort(request.numbers());
        return Result.ok(Map.of(
            "algorithm", "bubble-sort",
            "input", request.numbers(),
            "sorted", sorted
        ));
    }

    public record HashRequest(String text) {
    }

    public record SortRequest(int[] numbers) {
    }
}
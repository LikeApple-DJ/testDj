package com.example.demo.controller;

import com.example.demo.dto.SortRequest;
import com.example.demo.dto.SortResponse;
import com.example.demo.service.BubbleSortService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class BubbleSortController {

    private final BubbleSortService bubbleSortService;

    public BubbleSortController(BubbleSortService bubbleSortService) {
        this.bubbleSortService = bubbleSortService;
    }

    @PostMapping("/bubble-sort")
    public SortResponse sort(@RequestBody SortRequest request) {
        return bubbleSortService.bubbleSort(request.getArray());
    }
}

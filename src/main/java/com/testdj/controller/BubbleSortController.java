package com.testdj.controller;

import com.testdj.dto.BubbleSortRequest;
import com.testdj.service.BubbleSortService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class BubbleSortController {

    private final BubbleSortService bubbleSortService;

    public BubbleSortController(BubbleSortService bubbleSortService) {
        this.bubbleSortService = bubbleSortService;
    }

    @PostMapping("/bubble-sort")
    public Map<String, Object> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        return bubbleSortService.sort(request.getArray());
    }
}
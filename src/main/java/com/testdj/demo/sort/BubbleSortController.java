package com.testdj.demo.sort;

import com.testdj.demo.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
public class BubbleSortController {

    private final BubbleSortService bubbleSortService;

    public BubbleSortController(BubbleSortService bubbleSortService) {
        this.bubbleSortService = bubbleSortService;
    }

    @PostMapping("/sort/bubble")
    public ApiResponse<SortResponse> sort(@RequestBody SortRequest request) {
        return ApiResponse.ok(bubbleSortService.sort(request));
    }
}
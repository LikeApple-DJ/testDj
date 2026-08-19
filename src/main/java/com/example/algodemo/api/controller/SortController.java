package com.example.algodemo.api.controller;

import com.example.algodemo.api.request.SortRequest;
import com.example.algodemo.common.response.ApiResponse;
import com.example.algodemo.service.SortService;
import com.example.algodemo.service.model.SortResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 冒泡排序接口。
 */
@RestController
public class SortController {

    private final SortService sortService;

    public SortController(SortService sortService) {
        this.sortService = sortService;
    }

    @PostMapping({"/api/sort/bubble", "/openapi/sort/bubble"})
    public ApiResponse<SortResult> bubbleSort(@RequestBody SortRequest request) {
        SortResult result = sortService.bubbleSort(request.getArray(), request.getOrder());
        return ApiResponse.success(result);
    }
}

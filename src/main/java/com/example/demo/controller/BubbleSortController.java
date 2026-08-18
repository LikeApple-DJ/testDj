package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import com.example.demo.dto.BubbleSortRequest;
import com.example.demo.dto.BubbleSortResponse;
import com.example.demo.service.BubbleSortService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BubbleSortController {

    private final BubbleSortService bubbleSortService;

    public BubbleSortController(BubbleSortService bubbleSortService) {
        this.bubbleSortService = bubbleSortService;
    }

    @PostMapping("/bubblesort")
    public ResponseEntity<ApiResult<BubbleSortResponse>> bubbleSort(
            @RequestBody BubbleSortRequest request) {
        try {
            List<Integer> array = request.getArray();
            if (array == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResult.error(400, "array 不能为空"));
            }
            int[] arr = array.stream().mapToInt(i -> i).toArray();
            BubbleSortResponse data = bubbleSortService.sort(arr, request.getOrder());
            return ResponseEntity.ok(ApiResult.success(data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResult.error(400, e.getMessage()));
        }
    }
}
package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import com.example.demo.dto.BubbleSortRequest;
import com.example.demo.dto.BubbleSortResponse;
import com.example.demo.service.BubbleSortService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BubbleSortController {

    private static final Logger log = LoggerFactory.getLogger(BubbleSortController.class);

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
            if (request.getOrder() == null || request.getOrder().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(ApiResult.error(400, "order 不能为空，请指定 asc 或 desc"));
            }
            int[] arr = array.stream().mapToInt(i -> i).toArray();
            BubbleSortResponse data = bubbleSortService.sort(arr, request.getOrder());
            return ResponseEntity.ok(ApiResult.success(data));
        } catch (IllegalArgumentException e) {
            log.error("冒泡排序参数非法", e);
            return ResponseEntity.badRequest()
                    .body(ApiResult.error(400, e.getMessage()));
        }
    }
}
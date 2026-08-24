package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.SortService;
import com.example.demo.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SortController {

    @Autowired
    private SortService sortService;

    @Autowired
    private TrackingService trackingService;

    @PostMapping("/sort")
    public Result<SortService.SortResult> sort(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "X-Caller-Name", required = false) String callerName,
            @RequestHeader(value = "X-Caller-Type", required = false) String callerType,
            @RequestHeader(value = "X-Caller-Level", required = false) String callerLevel,
            @RequestHeader(value = "X-Caller-Dept", required = false) String callerDept) {

        Object numbersObj = body.get("numbers");
        String order = (String) body.get("order");

        if (numbersObj == null) {
            return Result.badRequest("numbers must not be empty");
        }

        @SuppressWarnings("unchecked")
        List<Integer> numbers = (List<Integer>) numbersObj;

        if (numbers.isEmpty()) {
            return Result.badRequest("numbers must not be empty");
        }

        SortService.SortResult result = sortService.bubbleSort(numbers, order);

        trackingService.record("sort", callerName, callerType, callerLevel, callerDept,
                "order=" + (order != null ? order : "asc") + ", size=" + numbers.size());

        return Result.success(result);
    }
}
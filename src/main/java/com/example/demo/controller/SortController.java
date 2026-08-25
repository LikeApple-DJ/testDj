package com.example.demo.controller;

import com.example.demo.service.AlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sort")
public class SortController {

    @Autowired
    private AlgorithmService algorithmService;

    @PostMapping("/bubble")
    public Map<String, Object> bubbleSort(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Integer> list = (List<Integer>) request.get("array");
        int[] array = list.stream().mapToInt(Integer::intValue).toArray();

        long startTime = System.currentTimeMillis();
        int[] sorted = algorithmService.bubbleSort(array);
        long duration = System.currentTimeMillis() - startTime;

        Map<String, Object> result = new HashMap<>();
        result.put("original", list);
        result.put("sorted", java.util.Arrays.stream(sorted).boxed().collect(Collectors.toList()));
        result.put("duration", duration);
        return result;
    }
}
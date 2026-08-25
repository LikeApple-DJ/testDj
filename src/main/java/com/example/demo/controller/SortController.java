package com.example.demo.controller;

import com.example.demo.service.AlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> bubbleSort(@RequestBody Map<String, Object> request) {
        try {
            Object arrayObj = request.get("array");
            if (arrayObj == null || !(arrayObj instanceof List)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "array must be a non-null list", "status", 400));
            }

            @SuppressWarnings("unchecked")
            List<Integer> list = (List<Integer>) arrayObj;
            int[] array = list.stream().mapToInt(Integer::intValue).toArray();

            long startTime = System.currentTimeMillis();
            int[] sorted = algorithmService.bubbleSort(array);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("original", list);
            result.put("sorted", java.util.Arrays.stream(sorted).boxed().collect(Collectors.toList()));
            result.put("duration", duration);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid array format", "status", 400));
        }
    }
}
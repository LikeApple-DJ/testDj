package com.testdj.controller;

import com.testdj.dto.SortRequest;
import com.testdj.service.SortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bubble-sort")
public class SortController {

    @Autowired
    private SortService sortService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sort(@RequestBody SortRequest request) {
        List<Integer> original = request.getArray();
        List<Integer> sorted = sortService.bubbleSort(original);
        return ResponseEntity.ok(Map.of(
                "tab", "sort",
                "original", original,
                "sorted", sorted,
                "length", original != null ? original.size() : 0
        ));
    }
}
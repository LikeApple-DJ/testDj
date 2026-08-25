package com.example.demo.controller;
import com.example.demo.service.BubbleSortService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class BubbleSortController {
    private final BubbleSortService bubbleSortService;
    public BubbleSortController(BubbleSortService bubbleSortService) { this.bubbleSortService = bubbleSortService; }
    @PostMapping("/bubblesort")
    public ResponseEntity<?> sort(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> arrayList = (List<Integer>) body.get("array");
        if (arrayList == null || arrayList.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("code", "BIZ_002", "message", "数组不能为空"));
        }
        int[] array = arrayList.stream().mapToInt(Integer::intValue).toArray();
        int[] sorted = bubbleSortService.sort(array);
        return ResponseEntity.ok(Map.of("original", array, "sorted", sorted));
    }
}
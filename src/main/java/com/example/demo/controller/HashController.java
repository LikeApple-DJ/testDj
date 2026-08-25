package com.example.demo.controller;

import com.example.demo.service.AlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HashController {

    @Autowired
    private AlgorithmService algorithmService;

    @PostMapping("/hash")
    public ResponseEntity<?> hash(@RequestBody Map<String, Object> request) {
        try {
            String input = (String) request.get("input");
            if (input == null || input.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "input cannot be empty", "status", 400));
            }

            String algorithm = (String) request.get("algorithm");
            String hashValue = algorithmService.hash(input, algorithm);

            Map<String, Object> result = new HashMap<>();
            result.put("input", input);
            result.put("algorithm", algorithm != null ? algorithm.toUpperCase() : "SHA-256");
            result.put("hash", hashValue);
            return ResponseEntity.ok(result);
        } catch (ClassCastException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid input format", "status", 400));
        }
    }
}
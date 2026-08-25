package com.example.demo.controller;

import com.example.demo.service.AlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HashController {

    @Autowired
    private AlgorithmService algorithmService;

    @PostMapping("/hash")
    public Map<String, Object> hash(@RequestBody Map<String, Object> request) {
        String input = (String) request.get("input");
        String algorithm = (String) request.get("algorithm");

        String hashValue = algorithmService.hash(input, algorithm);

        Map<String, Object> result = new HashMap<>();
        result.put("input", input);
        result.put("algorithm", algorithm != null ? algorithm.toUpperCase() : "SHA-256");
        result.put("hash", hashValue);
        return result;
    }
}
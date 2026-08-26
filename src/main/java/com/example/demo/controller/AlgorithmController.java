package com.example.demo.controller;

import com.example.demo.dto.HashRequest;
import com.example.demo.dto.SortRequest;
import com.example.demo.model.InvocationLog;
import com.example.demo.repository.InvocationLogRepository;
import com.example.demo.service.AlgorithmService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AlgorithmController {

    private final AlgorithmService algorithmService;
    private final InvocationLogRepository invocationLogRepository;

    public AlgorithmController(AlgorithmService algorithmService,
                               InvocationLogRepository invocationLogRepository) {
        this.algorithmService = algorithmService;
        this.invocationLogRepository = invocationLogRepository;
    }

    @GetMapping("/helloworld")
    public ResponseEntity<Map<String, String>> helloWorld() {
        logInvocation("/api/helloworld");
        return ResponseEntity.ok(algorithmService.helloWorld());
    }

    @PostMapping("/hash")
    public ResponseEntity<Map<String, Object>> hash(@RequestBody HashRequest request) {
        logInvocation("/api/hash");
        return ResponseEntity.ok(algorithmService.computeHash(request));
    }

    @PostMapping("/bubblesort")
    public ResponseEntity<Map<String, Object>> bubbleSort(@RequestBody SortRequest request) {
        logInvocation("/api/bubblesort");
        return ResponseEntity.ok(algorithmService.bubbleSort(request));
    }

    private void logInvocation(String api) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.getName() != null) ? auth.getName() : "anonymous";
        InvocationLog log = new InvocationLog(username, api, LocalDateTime.now());
        invocationLogRepository.save(log);
    }
}
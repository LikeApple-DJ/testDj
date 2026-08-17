package com.testdj.controller;

import com.testdj.dto.HashRequest;
import com.testdj.service.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/hash")
public class HashController {

    @Autowired
    private HashService hashService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> hash(@RequestBody HashRequest request) {
        String input = request.getInput() != null ? request.getInput() : "";
        String hashValue = hashService.sha256(input);
        return ResponseEntity.ok(Map.of(
                "tab", "hash",
                "algorithm", "SHA-256",
                "input", input,
                "hash", hashValue
        ));
    }
}
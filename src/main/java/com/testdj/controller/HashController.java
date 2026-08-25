package com.testdj.controller;

import com.testdj.dto.HashRequest;
import com.testdj.service.HashService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HashController {

    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping("/hash")
    public Map<String, Object> hash(@Valid @RequestBody HashRequest request) {
        return hashService.computeHash(request.getInput());
    }
}
package com.example.demo.controller;

import com.example.demo.dto.HashRequest;
import com.example.demo.dto.HashResponse;
import com.example.demo.service.HashService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class HashController {

    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping("/hash")
    public HashResponse hash(@RequestBody HashRequest request) {
        return hashService.computeHash(request.getInput(), request.getAlgorithm());
    }
}

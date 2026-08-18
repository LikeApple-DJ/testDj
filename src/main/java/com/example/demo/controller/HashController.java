package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import com.example.demo.dto.HashRequest;
import com.example.demo.dto.HashResponse;
import com.example.demo.service.HashService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HashController {

    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping("/hash")
    public ResponseEntity<ApiResult<HashResponse>> hash(@RequestBody HashRequest request) {
        try {
            String hash = hashService.compute(request.getInput(), request.getAlgorithm());
            HashResponse data = new HashResponse(
                    request.getInput(),
                    request.getAlgorithm().toUpperCase(),
                    hash);
            return ResponseEntity.ok(ApiResult.success(data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResult.error(400, e.getMessage()));
        }
    }
}
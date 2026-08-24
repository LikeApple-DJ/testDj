package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.HashService;
import com.example.demo.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HashController {

    @Autowired
    private HashService hashService;

    @Autowired
    private TrackingService trackingService;

    @PostMapping("/hash")
    public Result<Map<String, Object>> hash(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-Caller-Name", required = false) String callerName,
            @RequestHeader(value = "X-Caller-Type", required = false) String callerType,
            @RequestHeader(value = "X-Caller-Level", required = false) String callerLevel,
            @RequestHeader(value = "X-Caller-Dept", required = false) String callerDept) {

        String input = body.get("input");
        String algorithm = body.get("algorithm");

        if (input == null || input.isBlank()) {
            return Result.badRequest("input must not be empty");
        }
        if (algorithm == null || algorithm.isBlank()) {
            return Result.badRequest("algorithm must not be empty");
        }

        try {
            String hashOutput = hashService.hash(input, algorithm);

            trackingService.record("hash", callerName, callerType, callerLevel, callerDept,
                    "algorithm=" + algorithm + ", input=" + input);

            Map<String, Object> data = new java.util.HashMap<>();
            data.put("algorithm", algorithm.toUpperCase());
            data.put("input", input);
            data.put("output", hashOutput);

            return Result.success(data);
        } catch (IllegalArgumentException e) {
            return Result.badRequest(e.getMessage());
        } catch (Exception e) {
            return Result.serverError("Hash computation failed: " + e.getMessage());
        }
    }
}
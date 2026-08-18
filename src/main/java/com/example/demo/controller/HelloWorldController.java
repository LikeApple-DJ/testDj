package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/helloworld")
    public ApiResult<Map<String, Object>> helloWorld() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("message", "Hello World");
        data.put("timestamp", Instant.now().toString());
        return ApiResult.success(data);
    }
}
package com.testdj.controller;

import com.testdj.dto.HelloRequest;
import com.testdj.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @Autowired
    private HelloService helloService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> hello(@RequestBody HelloRequest request) {
        String message = helloService.greet(request.getName());
        return ResponseEntity.ok(Map.of(
                "tab", "hello",
                "message", message,
                "input", request.getName() != null ? request.getName() : ""
        ));
    }
}
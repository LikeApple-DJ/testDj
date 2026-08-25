package com.example.demo.controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class HelloWorldController {
    @GetMapping("/helloworld")
    public Map<String, String> hello(@RequestParam(defaultValue = "World") String name) {
        return Map.of("result", "Hello, " + name + "!");
    }
}
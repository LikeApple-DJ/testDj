package com.example.demo.controller;

import com.example.demo.dto.HelloRequest;
import com.example.demo.dto.HelloResponse;
import com.example.demo.service.HelloService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @PostMapping("/hello")
    public HelloResponse hello(@RequestBody HelloRequest request) {
        return helloService.sayHello(request.getName());
    }
}

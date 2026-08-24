package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.HelloService;
import com.example.demo.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Autowired
    private HelloService helloService;

    @Autowired
    private TrackingService trackingService;

    @GetMapping("/hello")
    public Result<String> hello(
            @RequestParam(value = "name", defaultValue = "World") String name,
            @RequestHeader(value = "X-Caller-Name", required = false) String callerName,
            @RequestHeader(value = "X-Caller-Type", required = false) String callerType,
            @RequestHeader(value = "X-Caller-Level", required = false) String callerLevel,
            @RequestHeader(value = "X-Caller-Dept", required = false) String callerDept) {

        String greeting = helloService.greet(name);

        trackingService.record("hello", callerName, callerType, callerLevel, callerDept, "name=" + name);

        return Result.success(greeting);
    }
}
package com.example.algodemo.api.controller;

import com.example.algodemo.common.response.ApiResponse;
import com.example.algodemo.service.HelloWorldService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * HelloWorld 接口。
 */
@RestController
public class HelloWorldController {

    private final HelloWorldService helloWorldService;

    public HelloWorldController(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @GetMapping({"/api/hello", "/openapi/hello"})
    public ApiResponse<Map<String, String>> sayHello(
            @RequestParam(value = "name", required = false) String name) {
        Map<String, String> data = new HashMap<>(1);
        data.put("greeting", helloWorldService.sayHello(name));
        return ApiResponse.success(data);
    }
}

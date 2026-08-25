package com.testdj.demo.hello;

import com.testdj.demo.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
public class HelloController {

    @GetMapping("/hello")
    public ApiResponse<String> hello() {
        return ApiResponse.ok("Hello, World!");
    }
}
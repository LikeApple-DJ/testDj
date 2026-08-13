package com.example.demo.service;

import com.example.demo.dto.HelloResponse;
import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public HelloResponse sayHello(String name) {
        String greeting = "Hello, " + name + "!";
        return new HelloResponse(greeting);
    }
}

package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String greet(String name) {
        if (name == null || name.isBlank()) {
            name = "World";
        }
        return "Hello, " + name + "!";
    }
}
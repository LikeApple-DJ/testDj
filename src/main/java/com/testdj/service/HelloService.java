package com.testdj.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String greet(String name) {
        if (name == null || name.trim().isEmpty()) {
            name = "World";
        }
        return "Hello, " + name.trim() + "!";
    }
}
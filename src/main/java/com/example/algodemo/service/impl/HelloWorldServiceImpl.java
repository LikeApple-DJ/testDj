package com.example.algodemo.service.impl;

import com.example.algodemo.service.HelloWorldService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * HelloWorld 服务实现。
 */
@Service
public class HelloWorldServiceImpl implements HelloWorldService {

    private static final String DEFAULT_NAME = "World";

    @Override
    public String sayHello(String name) {
        String targetName = Objects.toString(name, DEFAULT_NAME);
        if (targetName.trim().isEmpty()) {
            targetName = DEFAULT_NAME;
        }
        return "Hello, " + targetName + "!";
    }
}

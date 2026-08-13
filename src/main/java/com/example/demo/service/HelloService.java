package com.example.demo.service;

import com.example.demo.dto.HelloResponse;
import com.example.demo.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public HelloResponse sayHello(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("BIZ_001", "name 不能为空或空白字符串");
        }
        if (name.length() > 100) {
            throw new BusinessException("BIZ_001", "name 长度不超过 100 字符");
        }
        String greeting = "Hello, " + name + "!";
        return new HelloResponse(greeting);
    }
}

package com.testdj.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class HelloService {

    public Map<String, Object> getHello() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Hello World!");
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return result;
    }
}
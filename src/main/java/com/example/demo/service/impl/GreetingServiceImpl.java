package com.example.demo.service.impl;

import com.example.demo.model.vo.GreetingVO;
import com.example.demo.service.GreetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 问候服务实现类。
 *
 * @author AiWork
 */
@Service
public class GreetingServiceImpl implements GreetingService {

    private static final Logger log = LoggerFactory.getLogger(GreetingServiceImpl.class);

    /** 默认问候前缀 */
    private static final String DEFAULT_PREFIX = "Hello";

    /** 默认用户名（当 name 为空时使用） */
    private static final String DEFAULT_NAME = "Stranger";

    @Override
    public GreetingVO greet(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        String effectiveName = name.isBlank() ? DEFAULT_NAME : name.trim();
        String message = DEFAULT_PREFIX + ", " + effectiveName + "!";
        log.debug("Generated greeting for name: {}", effectiveName);

        return new GreetingVO(message, effectiveName, LocalDateTime.now());
    }

    @Override
    public GreetingVO greetWithFormat(String name, String prefix) {
        String effectivePrefix = (prefix == null || prefix.isBlank()) ? DEFAULT_PREFIX : prefix.trim();
        String effectiveName = (name == null || name.isBlank()) ? DEFAULT_NAME : name.trim();
        String message = effectivePrefix + ", " + effectiveName + "!";

        return new GreetingVO(message, effectiveName, LocalDateTime.now());
    }
}
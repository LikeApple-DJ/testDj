package com.example.demo.controller;

import com.example.demo.model.vo.GreetingVO;
import com.example.demo.service.GreetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问候语 REST 控制器。
 *
 * @author AiWork
 */
@RestController
@RequestMapping("/api/greeting")
public class GreetingController {

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * 获取问候语。
     *
     * @param name 用户名，可选，默认为 "Stranger"
     * @return 问候语视图对象
     */
    @GetMapping
    public GreetingVO greet(@RequestParam(defaultValue = "Stranger") String name) {
        log.info("Received greeting request for name: {}", name);
        return greetingService.greet(name);
    }
}
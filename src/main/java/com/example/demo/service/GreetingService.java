package com.example.demo.service;

import com.example.demo.model.vo.GreetingVO;

/**
 * 问候服务接口。
 *
 * @author AiWork
 */
public interface GreetingService {

    /**
     * 根据名称生成问候语。
     *
     * @param name 用户名，不能为 null
     * @return 问候语视图对象
     * @throws IllegalArgumentException 当 name 为 null 时
     */
    GreetingVO greet(String name);

    /**
     * 根据名称和格式前缀生成问候语。
     *
     * @param name   用户名，不能为 null
     * @param prefix 问候前缀，为 null 或空时使用默认值 "Hello"
     * @return 问候语视图对象
     */
    GreetingVO greetWithFormat(String name, String prefix);
}
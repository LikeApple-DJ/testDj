package com.example.algodemo.service;

/**
 * HelloWorld 服务。
 */
public interface HelloWorldService {

    /**
     * 返回问候语。
     *
     * @param name 称呼，为空时使用默认值 "World"
     * @return 问候语
     */
    String sayHello(String name);
}

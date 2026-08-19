package com.dtcode.demo.demo.model.dto;

/**
 * HelloWorld 请求参数
 *
 * @author DTCoder
 */
public class HelloWorldRequest {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "HelloWorldRequest{name='" + name + "'}";
    }
}

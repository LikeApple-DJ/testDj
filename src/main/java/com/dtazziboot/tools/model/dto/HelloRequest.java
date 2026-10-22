package com.dtazziboot.tools.model.dto;

import java.io.Serializable;

/**
 * helloworld 请求参数。
 */
public class HelloRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 问候对象名，可空，默认 "World"。 */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
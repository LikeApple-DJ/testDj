package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HelloRequest {

    @NotBlank(message = "name 不能为空或空白字符串")
    @Size(max = 100, message = "name 长度不超过 100 字符")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

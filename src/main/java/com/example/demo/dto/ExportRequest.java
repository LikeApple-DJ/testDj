package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 导出请求 DTO。
 */
public class ExportRequest {

    @NotBlank(message = "type 不能为空")
    private String type;

    @NotNull(message = "data 不能为 null")
    private Object data;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
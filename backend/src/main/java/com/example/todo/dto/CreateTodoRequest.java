package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTodoRequest {

    @NotBlank(message = "事项名称不能为空")
    @Size(min = 1, max = 255, message = "事项名称长度需在 1-255 之间")
    private String name;

    @Size(max = 5000, message = "描述长度不能超过 5000")
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
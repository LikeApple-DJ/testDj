package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTodoRequest {

    @NotBlank(message = "事项名称不能为空")
    @Size(max = 200, message = "事项名称最多200个字符")
    private String name;

    @Size(max = 2000, message = "描述最多2000个字符")
    private String description;

    public CreateTodoRequest() {}

    public CreateTodoRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
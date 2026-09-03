package com.example.todo.todo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建待办事项 - 请求DTO
 */
public class CreateTodoRequest {

    @NotBlank(message = "事项名称不能为空")
    @Size(max = 100, message = "事项名称长度不能超过100字符")
    private String title;

    @Size(max = 500, message = "事项描述长度不能超过500字符")
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
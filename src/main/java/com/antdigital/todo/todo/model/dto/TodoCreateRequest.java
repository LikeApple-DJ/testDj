package com.antdigital.todo.todo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * 新增待办事项请求 DTO
 *
 * @author AiWork
 * @date 2026/08/31
 */
public class TodoCreateRequest {

    /** 事项名称，必填，≤100 字符，不可全空白 */
    @NotBlank(message = "事项名称不可为空")
    @Size(max = 100, message = "事项名称不可超过100字符")
    private String name;

    /** 事项描述，选填，≤500 字符 */
    @Size(max = 500, message = "事项描述不可超过500字符")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TodoCreateRequest that = (TodoCreateRequest) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        return "TodoCreateRequest{name='" + name + "', description='" + description + "'}";
    }
}

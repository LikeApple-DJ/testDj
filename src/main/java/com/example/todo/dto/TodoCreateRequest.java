package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * 新增待办事项请求入参。
 *
 * <p>对应 design.md §5.2.2 W01 入参：name（必填，1~200 字符）、description（选填，0~2000 字符）。</p>
 */
public class TodoCreateRequest {

    /** 事项名称 */
    @NotBlank
    @Size(max = 200)
    private String name;

    /** 事项描述 */
    @Size(max = 2000)
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
    public String toString() {
        return "TodoCreateRequest{"
                + "name='" + name + '\''
                + ", description='" + description + '\''
                + '}';
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
}

package com.antdigital.todo.todo.model.dto;

import java.util.Objects;

/**
 * 新增待办事项结果 DTO
 *
 * @author AiWork
 * @date 2026/08/31
 */
public class TodoCreateResult {

    /** 创建后的待办事项主键 ID */
    private Long id;

    public TodoCreateResult() {
    }

    public TodoCreateResult(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TodoCreateResult that = (TodoCreateResult) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TodoCreateResult{id=" + id + '}';
    }
}

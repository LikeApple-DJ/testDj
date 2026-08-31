package com.antdigital.todo.model.dto;

import java.io.Serializable;

/**
 * 新增待办事项响应 DTO。
 *
 * <p>对应 design.md §5.1.2 W01 出参：data.id（新建待办事项ID）。</p>
 */
public class TodoCreateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新建待办事项ID */
    private Long id;

    public TodoCreateResponse() {
    }

    public TodoCreateResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TodoCreateResponse{id=" + id + '}';
    }
}

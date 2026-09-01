package com.aiwork.todo.model.dto;

/**
 * 新增待办事项结果对象
 *
 * @author AiWork
 * @date 2026/09/01
 */
public class CreateTodoResult {

    /** 生成的事项 ID */
    private Long id;

    public CreateTodoResult() {
    }

    public CreateTodoResult(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

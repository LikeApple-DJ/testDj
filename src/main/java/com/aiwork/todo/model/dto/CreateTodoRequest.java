package com.aiwork.todo.model.dto;

/**
 * 新增待办事项请求对象
 *
 * <p>入参：事项名称（必填，1-100 字符）+ 描述（选填，最长 1000 字符）。</p>
 *
 * @author AiWork
 * @date 2026/09/01
 */
public class CreateTodoRequest {

    /** 事项名称 */
    private String title;

    /** 事项描述 */
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

    @Override
    public String toString() {
        return "CreateTodoRequest{"
                + "title='" + title + '\''
                + ", descriptionLength=" + (description == null ? 0 : description.length())
                + '}';
    }
}

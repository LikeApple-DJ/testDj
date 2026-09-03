package com.example.todo.todo.service;

import com.example.todo.todo.model.dto.CreateTodoRequest;

/**
 * 待办事项 - 业务服务接口
 */
public interface TodoService {

    /**
     * 创建待办事项
     *
     * @param request 创建请求
     * @return 新建待办事项的ID
     */
    Long createTodo(CreateTodoRequest request);
}
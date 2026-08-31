package com.example.todo.service;

import com.example.todo.dto.TodoCreateRequest;
import com.example.todo.dto.TodoCreateResult;

/**
 * 待办事项业务服务。
 *
 * <p>对应 design.md §4.3 S01：createTodo(TodoCreateRequest)。</p>
 */
public interface TodoService {

    /**
     * 创建待办事项，持久化名称与描述，返回事项ID与创建时间。
     *
     * @param request 创建请求
     * @return 创建结果
     */
    TodoCreateResult createTodo(TodoCreateRequest request);
}

package com.antdigital.todo.service;

import com.antdigital.todo.model.dto.TodoItemCreateRequest;

/**
 * 待办事项业务服务接口
 *
 * @author AiWork
 * @date 2026/08/31
 */
public interface TodoItemService {

    /**
     * 创建待办事项，持久化保存并返回事项ID
     *
     * @param request 创建请求，包含事项名称和描述
     * @return 创建成功的待办事项ID
     */
    Long createTodoItem(TodoItemCreateRequest request);
}

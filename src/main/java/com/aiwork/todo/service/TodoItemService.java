package com.aiwork.todo.service;

import com.aiwork.todo.model.dto.CreateTodoRequest;
import com.aiwork.todo.model.dto.CreateTodoResult;

/**
 * 待办事项业务服务
 *
 * @author AiWork
 * @date 2026/09/01
 */
public interface TodoItemService {

    /**
     * 创建待办事项：校验入参、写入默认状态并落库，返回生成的事项 ID
     *
     * @param request 新增待办事项请求（名称必填、描述选填）
     * @return 创建结果，含生成的事项 ID
     */
    CreateTodoResult createTodoItem(CreateTodoRequest request);
}

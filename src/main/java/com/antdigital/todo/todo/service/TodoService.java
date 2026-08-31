package com.antdigital.todo.todo.service;

import com.antdigital.todo.todo.model.dto.TodoCreateRequest;
import com.antdigital.todo.todo.model.dto.TodoCreateResult;

/**
 * 待办事项业务服务接口
 *
 * @author AiWork
 * @date 2026/08/31
 */
public interface TodoService {

    /**
     * 创建待办事项：校验业务规则、注入租户标识、持久化并返回主键 ID
     *
     * @param request 创建请求，包含事项名称与描述
     * @return 创建结果，含生成的主键 ID
     */
    TodoCreateResult createTodo(TodoCreateRequest request);
}

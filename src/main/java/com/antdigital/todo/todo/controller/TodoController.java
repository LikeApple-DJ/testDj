package com.antdigital.todo.todo.controller;

import com.antdigital.todo.common.model.ApiResponse;
import com.antdigital.todo.todo.model.dto.TodoCreateRequest;
import com.antdigital.todo.todo.model.dto.TodoCreateResult;
import com.antdigital.todo.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项控制器
 *
 * @author AiWork
 * @date 2026/08/31
 */
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 新增待办事项
     *
     * @param request 创建请求，包含事项名称与描述
     * @return 通用响应，data 含创建后的待办事项 ID
     */
    @PostMapping
    public ApiResponse<TodoCreateResult> createTodo(@Valid @RequestBody TodoCreateRequest request) {
        TodoCreateResult result = todoService.createTodo(request);
        return ApiResponse.success(result);
    }
}

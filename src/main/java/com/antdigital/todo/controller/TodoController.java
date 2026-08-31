package com.antdigital.todo.controller;

import com.antdigital.todo.common.ApiResponse;
import com.antdigital.todo.model.dto.TodoCreateRequest;
import com.antdigital.todo.model.dto.TodoCreateResponse;
import com.antdigital.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项控制器。
 *
 * <p>对应 design.md §4.1 W01：POST /api/todo/create。</p>
 */
@RestController
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 新增待办事项。
     *
     * <p>内部用户新增一条待办事项，状态默认置为"待处理"。</p>
     *
     * @param request 新增请求（name 必填 1-128，description 选填 0-1024）
     * @return 通用响应，data.id 为新建待办事项ID
     */
    @PostMapping("/create")
    public ApiResponse<TodoCreateResponse> createTodo(@Valid @RequestBody TodoCreateRequest request) {
        TodoCreateResponse response = todoService.createTodo(request);
        return ApiResponse.success(response);
    }
}

package com.example.todo.controller;

import com.example.todo.dto.ApiResponse;
import com.example.todo.dto.TodoCreateRequest;
import com.example.todo.dto.TodoCreateResult;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项控制器。
 *
 * <p>对应 design.md §5.2.2 W01：POST /api/todo。</p>
 */
@RestController
@RequestMapping("/api")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 新增待办事项。
     *
     * @param request 创建请求
     * @return 通用响应包装的创建结果
     */
    @PostMapping("/todo")
    public ApiResponse<TodoCreateResult> createTodo(@Valid @RequestBody TodoCreateRequest request) {
        return ApiResponse.success(todoService.createTodo(request));
    }
}

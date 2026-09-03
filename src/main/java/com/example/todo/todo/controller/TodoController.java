package com.example.todo.todo.controller;

import com.example.todo.common.response.ApiResponse;
import com.example.todo.todo.model.dto.CreateTodoRequest;
import com.example.todo.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 待办事项 - 控制器
 */
@RestController
@RequestMapping("/api/todo")
public class TodoController {

    private static final Logger log = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 新增待办事项
     *
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public ApiResponse<Map<String, Long>> createTodo(@Valid @RequestBody CreateTodoRequest request) {
        log.info("新增待办事项请求: title={}, description={}", request.getTitle(), request.getDescription());

        Long id = todoService.createTodo(request);
        return ApiResponse.success(Map.of("id", id));
    }
}
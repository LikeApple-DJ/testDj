package com.example.todo.todo.controller;

import com.example.todo.common.response.ApiResponse;
import com.example.todo.todo.model.dto.CreateTodoRequest;
import com.example.todo.todo.service.TodoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        // 当前版本暂未接入统一登录态，使用固定用户ID
        // 后续接入统一登录态后，从 SecurityContext / Request 中获取
        String userId = "SYSTEM";
        log.info("新增待办事项请求: title={}, description={}", request.getTitle(), request.getDescription());

        Long id = todoService.createTodo(request, userId);
        return ApiResponse.success(Map.of("id", id));
    }
}
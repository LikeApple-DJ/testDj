package com.antdigital.todo.controller;

import com.antdigital.todo.common.response.ApiResponse;
import com.antdigital.todo.model.dto.TodoItemCreateRequest;
import com.antdigital.todo.model.vo.TodoItemVO;
import com.antdigital.todo.service.TodoItemService;
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
@RequestMapping("/api/todo")
public class TodoItemController {

    private final TodoItemService todoItemService;

    /**
     * 构造注入 Service
     *
     * @param todoItemService 待办事项服务
     */
    public TodoItemController(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    /**
     * 新增待办事项
     * <p>
     * POST /api/todo/items
     * 录入事项名称和描述，系统持久化保存，返回事项ID。
     * </p>
     *
     * @param request 创建请求
     * @return 包含事项ID的响应
     */
    @PostMapping("/items")
    public ApiResponse<TodoItemVO> createTodoItem(@Valid @RequestBody TodoItemCreateRequest request) {
        Long id = todoItemService.createTodoItem(request);
        return ApiResponse.success(new TodoItemVO(id));
    }
}

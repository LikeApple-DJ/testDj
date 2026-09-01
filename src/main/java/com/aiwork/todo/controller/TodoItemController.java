package com.aiwork.todo.controller;

import com.aiwork.todo.common.exception.TodoErrorCodeEnum;
import com.aiwork.todo.common.result.Result;
import com.aiwork.todo.model.dto.CreateTodoRequest;
import com.aiwork.todo.model.dto.CreateTodoResult;
import com.aiwork.todo.service.TodoItemService;
import com.aiwork.todo.common.constant.TodoConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项控制器
 *
 * <p>对外接口 O01：POST /openapi/todo/items。</p>
 *
 * @author AiWork
 * @date 2026/09/01
 */
@RestController
@RequestMapping("/openapi/todo/items")
public class TodoItemController {

    private final TodoItemService todoItemService;

    /** 变更三板斧-可应急：新增待办事项功能开关 */
    @Value("${todo.create.enabled:true}")
    private boolean createEnabled;

    public TodoItemController(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    /**
     * 新增待办事项
     *
     * @param request 新增请求（名称必填、描述选填）
     * @return 创建结果，含生成的事项 ID
     */
    @PostMapping
    public ResponseEntity<Result<CreateTodoResult>> createTodoItem(@RequestBody CreateTodoRequest request) {
        if (!createEnabled) {
            return ResponseEntity.ok(Result.error(
                    TodoErrorCodeEnum.TODO_999.getCode(), TodoConstants.CREATE_DISABLED_MSG));
        }
        CreateTodoResult result = todoItemService.createTodoItem(request);
        return ResponseEntity.ok(Result.success(result));
    }
}

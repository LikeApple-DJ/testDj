package com.org.module.controller;

import com.org.module.Result;
import com.org.module.dto.TodoDTO;
import com.org.module.service.TodoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项接口
 */
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private static final Logger log = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 新增待办事项
     *
     * @param dto 待办事项创建请求（title 必填、description 选填）
     * @return 创建结果
     */
    @PostMapping
    public Result<Void> create(@RequestBody @Valid TodoDTO dto) {
        // 日志仅打印 title，避免打印长描述
        log.info("新增待办事项: title={}", dto.getTitle());
        todoService.createTodo(dto);
        return Result.ok();
    }
}

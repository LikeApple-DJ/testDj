package com.org.module.controller;

import com.org.module.Result;
import com.org.module.dto.TodoDTO;
import com.org.module.dto.TodoVO;
import com.org.module.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项控制器
 */
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public Result<TodoVO> create(@RequestBody @Valid TodoDTO dto) {
        TodoVO vo = todoService.createTodo(dto);
        return Result.ok(vo);
    }
}

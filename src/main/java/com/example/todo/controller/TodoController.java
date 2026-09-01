package com.example.todo.controller;

import com.example.todo.dto.ApiResponse;
import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.model.TodoItem;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TodoItem> create(@Valid @RequestBody CreateTodoRequest request) {
        TodoItem item = service.create(request.getName(), request.getDescription());
        return ApiResponse.success(item);
    }
}
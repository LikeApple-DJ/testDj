package com.example.todo.controller;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.entity.TodoItem;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<TodoItem> create(@Valid @RequestBody CreateTodoRequest request) {
        TodoItem created = todoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TodoItem>> listAll() {
        return ResponseEntity.ok(todoService.listAll());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "输入校验失败";
        Map<String, String> body = new HashMap<>();
        body.put("error", "VALIDATION_ERROR");
        body.put("message", message);
        return ResponseEntity.badRequest().body(body);
    }
}
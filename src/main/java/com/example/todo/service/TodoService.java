package com.example.todo.service;

import com.example.todo.model.TodoItem;
import com.example.todo.repository.TodoItemRepository;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    private final TodoItemRepository repository;

    public TodoService(TodoItemRepository repository) {
        this.repository = repository;
    }

    public TodoItem create(String name, String description) {
        TodoItem item = new TodoItem(name, description);
        return repository.save(item);
    }
}
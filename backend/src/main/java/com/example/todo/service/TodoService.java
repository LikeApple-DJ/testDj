package com.example.todo.service;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.entity.TodoItem;
import com.example.todo.repository.TodoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoItem create(CreateTodoRequest request) {
        TodoItem item = new TodoItem();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        return todoRepository.save(item);
    }

    public List<TodoItem> listAll() {
        return todoRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
package com.example.todo.service;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.entity.TodoItem;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void shouldCreateTodoItem() {
        CreateTodoRequest request = new CreateTodoRequest();
        request.setName("Buy milk");
        request.setDescription("Get 2% milk");

        TodoItem savedItem = new TodoItem();
        savedItem.setId(1L);
        savedItem.setName("Buy milk");
        savedItem.setDescription("Get 2% milk");
        savedItem.setCreatedAt(LocalDateTime.now());

        when(todoRepository.save(any(TodoItem.class))).thenReturn(savedItem);

        TodoItem result = todoService.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Buy milk");
        assertThat(result.getDescription()).isEqualTo("Get 2% milk");

        ArgumentCaptor<TodoItem> captor = ArgumentCaptor.forClass(TodoItem.class);
        verify(todoRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Buy milk");
    }

    @Test
    void shouldListAllTodosDescending() {
        TodoItem item1 = new TodoItem();
        item1.setId(1L);
        item1.setName("First");
        TodoItem item2 = new TodoItem();
        item2.setId(2L);
        item2.setName("Second");

        when(todoRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")))
                .thenReturn(List.of(item2, item1));

        List<TodoItem> result = todoService.listAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Second");
        assertThat(result.get(1).getName()).isEqualTo("First");
    }
}
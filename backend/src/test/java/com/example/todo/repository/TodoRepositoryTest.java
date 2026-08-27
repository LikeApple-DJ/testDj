package com.example.todo.repository;

import com.example.todo.entity.TodoItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void shouldFindAllOrderByCreatedAtDesc() {
        TodoItem item1 = new TodoItem();
        item1.setName("First");
        item1.setDescription("First desc");
        item1.setCreatedAt(LocalDateTime.of(2026, 8, 27, 10, 0));

        TodoItem item2 = new TodoItem();
        item2.setName("Second");
        item2.setDescription("Second desc");
        item2.setCreatedAt(LocalDateTime.of(2026, 8, 27, 11, 0));

        todoRepository.save(item1);
        todoRepository.save(item2);

        List<TodoItem> todos = todoRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        assertThat(todos).hasSize(2);
        assertThat(todos.get(0).getName()).isEqualTo("Second");
        assertThat(todos.get(1).getName()).isEqualTo("First");
    }

    @Test
    void shouldSaveAndRetrieveTodoItem() {
        TodoItem item = new TodoItem();
        item.setName("Buy milk");
        item.setDescription("Get 2% milk from store");

        TodoItem saved = todoRepository.save(item);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Buy milk");
        assertThat(saved.getDescription()).isEqualTo("Get 2% milk from store");
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
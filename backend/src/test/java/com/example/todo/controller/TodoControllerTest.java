package com.example.todo.controller;

import com.example.todo.config.CorsConfig;
import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.entity.TodoItem;
import com.example.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
@Import(CorsConfig.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    @Test
    void shouldCreateTodoAndReturn201() throws Exception {
        CreateTodoRequest request = new CreateTodoRequest();
        request.setName("Buy milk");
        request.setDescription("Get 2% milk");

        TodoItem saved = new TodoItem();
        saved.setId(1L);
        saved.setName("Buy milk");
        saved.setDescription("Get 2% milk");
        saved.setCreatedAt(LocalDateTime.of(2026, 8, 27, 10, 30));

        when(todoService.create(any(CreateTodoRequest.class))).thenReturn(saved);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Buy milk"))
                .andExpect(jsonPath("$.description").value("Get 2% milk"))
                .andExpect(jsonPath("$.createdAt").value("2026-08-27T10:30:00"));
    }

    @Test
    void shouldRejectEmptyNameWith400() throws Exception {
        CreateTodoRequest request = new CreateTodoRequest();
        request.setName("");
        request.setDescription("Some desc");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldListAllTodosAndReturn200() throws Exception {
        TodoItem item1 = new TodoItem();
        item1.setId(1L);
        item1.setName("First");
        item1.setDescription("First desc");
        item1.setCreatedAt(LocalDateTime.of(2026, 8, 27, 10, 0));

        TodoItem item2 = new TodoItem();
        item2.setId(2L);
        item2.setName("Second");
        item2.setDescription("Second desc");
        item2.setCreatedAt(LocalDateTime.of(2026, 8, 27, 11, 0));

        when(todoService.listAll()).thenReturn(List.of(item2, item1));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Second"))
                .andExpect(jsonPath("$[1].id").value(1))
                .andExpect(jsonPath("$[1].name").value("First"));
    }
}
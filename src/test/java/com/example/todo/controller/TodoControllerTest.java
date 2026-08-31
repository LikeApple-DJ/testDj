package com.example.todo.controller;

import com.example.todo.dto.TodoCreateResult;
import com.example.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link TodoController} 切片测试：校验参数校验与错误码映射。
 */
@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Test
    void should_returnOk_when_requestIsValid() throws Exception {
        // Arrange
        TodoCreateResult result = new TodoCreateResult();
        result.setId(1001L);
        result.setName("完成周报");
        result.setDescription("本周工作总结与下周计划");
        result.setCreatorId(88L);
        when(todoService.createTodo(any())).thenReturn(result);

        // Act
        MvcResult mvcResult = mockMvc.perform(post("/api/todo")
                        .contentType("application/json")
                        .content("{\"name\":\"完成周报\",\"description\":\"本周工作总结与下周计划\"}"))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(body).contains("\"result\":\"OK\"");
        assertThat(body).contains("\"id\":1001");
        assertThat(body).contains("\"name\":\"完成周报\"");
    }

    @Test
    void should_returnNameEmpty_when_nameIsBlank() throws Exception {
        // Act
        MvcResult mvcResult = mockMvc.perform(post("/api/todo")
                        .contentType("application/json")
                        .content("{\"name\":\"   \",\"description\":\"x\"}"))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(body).contains("\"result\":\"TODO_0001\"");
    }

    @Test
    void should_returnNameTooLong_when_nameExceeds200() throws Exception {
        // Arrange - 201 字符
        String tooLongName = "a".repeat(201);

        // Act
        MvcResult mvcResult = mockMvc.perform(post("/api/todo")
                        .contentType("application/json")
                        .content("{\"name\":\"" + tooLongName + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(body).contains("\"result\":\"TODO_0002\"");
    }

    @Test
    void should_returnDescriptionTooLong_when_descriptionExceeds2000() throws Exception {
        // Arrange - 2001 字符
        String tooLongDesc = "a".repeat(2001);

        // Act
        MvcResult mvcResult = mockMvc.perform(post("/api/todo")
                        .contentType("application/json")
                        .content("{\"name\":\"ok\",\"description\":\"" + tooLongDesc + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String body = mvcResult.getResponse().getContentAsString();
        assertThat(body).contains("\"result\":\"TODO_0003\"");
    }
}

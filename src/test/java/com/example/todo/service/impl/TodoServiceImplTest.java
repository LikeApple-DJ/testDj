package com.example.todo.service.impl;

import com.example.todo.context.UserContext;
import com.example.todo.dto.TodoCreateRequest;
import com.example.todo.dto.TodoCreateResult;
import com.example.todo.exception.TodoErrorCode;
import com.example.todo.exception.TodoException;
import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TodoServiceImpl} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    private static final Long LOGIN_USER_ID = 88L;
    private static final Long SAVED_TODO_ID = 1001L;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private TodoServiceImpl todoService;

    // ==================== createTodo 测试 ====================

    @Test
    void should_returnCreateResult_when_requestIsValid() {
        // Arrange
        when(userContext.getCurrentUserId()).thenReturn(LOGIN_USER_ID);
        TodoCreateRequest request = buildRequest("完成周报", "本周工作总结与下周计划");
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> {
            Todo todo = invocation.getArgument(0);
            todo.setId(SAVED_TODO_ID);
            return todo;
        });

        // Act
        TodoCreateResult result = todoService.createTodo(request);

        // Assert
        assertThat(result.getId()).isEqualTo(SAVED_TODO_ID);
        assertThat(result.getName()).isEqualTo("完成周报");
        assertThat(result.getDescription()).isEqualTo("本周工作总结与下周计划");
        assertThat(result.getCreatorId()).isEqualTo(LOGIN_USER_ID);
        assertThat(result.getGmtCreate()).isNotNull();
    }

    @Test
    void should_persistTodoWithCorrectFields_when_requestIsValid() {
        // Arrange
        when(userContext.getCurrentUserId()).thenReturn(LOGIN_USER_ID);
        TodoCreateRequest request = buildRequest("写单测", null);
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> {
            Todo todo = invocation.getArgument(0);
            todo.setId(SAVED_TODO_ID);
            return todo;
        });

        // Act
        todoService.createTodo(request);

        // Assert - 验证落库实体的关键字段
        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        verify(todoRepository).save(captor.capture());
        Todo saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("写单测");
        assertThat(saved.getDescription()).isNull();
        assertThat(saved.getCreatorId()).isEqualTo(LOGIN_USER_ID);
        assertThat(saved.getTenantId()).isNotNull().isEqualTo(0L);
        assertThat(saved.getGmtCreate()).isNotNull();
        assertThat(saved.getGmtModified()).isNotNull().isEqualTo(saved.getGmtCreate());
    }

    @Test
    void should_setCreatorIdNull_when_userContextReturnsNull() {
        // Arrange
        when(userContext.getCurrentUserId()).thenReturn(null);
        TodoCreateRequest request = buildRequest("匿名事项", null);
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> {
            Todo todo = invocation.getArgument(0);
            todo.setId(SAVED_TODO_ID);
            return todo;
        });

        // Act
        TodoCreateResult result = todoService.createTodo(request);

        // Assert
        assertThat(result.getCreatorId()).isNull();
        assertThat(result.getId()).isEqualTo(SAVED_TODO_ID);
    }

    @Test
    void should_throwSystemError_when_repositoryThrowsDataAccessException() {
        // Arrange
        when(userContext.getCurrentUserId()).thenReturn(LOGIN_USER_ID);
        TodoCreateRequest request = buildRequest("失败事项", null);
        when(todoRepository.save(any(Todo.class)))
                .thenThrow(new DataAccessResourceFailureException("connection lost"));

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(TodoException.class)
                .hasMessageContaining(TodoErrorCode.SYSTEM_ERROR.getMessage())
                .satisfies(ex -> assertThat(((TodoException) ex).getErrorCode())
                        .isEqualTo(TodoErrorCode.SYSTEM_ERROR));
    }

    // ==================== 测试数据构造 ====================

    private static TodoCreateRequest buildRequest(String name, String description) {
        TodoCreateRequest request = new TodoCreateRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }
}

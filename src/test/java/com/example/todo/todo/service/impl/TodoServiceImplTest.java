package com.example.todo.todo.service.impl;

import com.example.todo.common.exception.BusinessException;
import com.example.todo.todo.dao.entity.TodoItemDO;
import com.example.todo.todo.dao.mapper.TodoItemMapper;
import com.example.todo.todo.model.dto.CreateTodoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TodoServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TodoServiceImpl - 待办事项服务实现")
class TodoServiceImplTest {

    @Mock
    private TodoItemMapper todoItemMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    @Nested
    @DisplayName("createTodo() - 创建待办事项")
    class CreateTodo {

        @Test
        @DisplayName("正常路径：创建成功，返回自增ID")
        void should_returnId_when_validRequest() {
            // Arrange
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("完成周报");
            request.setDescription("本周完成项目进度汇报");

            when(todoItemMapper.insert(any(TodoItemDO.class)))
                    .thenAnswer(invocation -> {
                        TodoItemDO item = invocation.getArgument(0);
                        item.setId(1L);
                        return 1;
                    });

            // Act
            Long result = todoService.createTodo(request);

            // Assert
            assertThat(result).isEqualTo(1L);

            ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
            verify(todoItemMapper).insert(captor.capture());
            TodoItemDO captured = captor.getValue();
            assertThat(captured.getUserId()).isEqualTo("SYSTEM");
            assertThat(captured.getTitle()).isEqualTo("完成周报");
            assertThat(captured.getDescription()).isEqualTo("本周完成项目进度汇报");
            assertThat(captured.getStatus()).isZero();
            assertThat(captured.getGmtCreate()).isNotNull();
            assertThat(captured.getGmtModified()).isNotNull();
        }

        @Test
        @DisplayName("正常路径：描述为空时，创建成功")
        void should_returnId_when_descriptionIsNull() {
            // Arrange
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("简单任务");

            when(todoItemMapper.insert(any(TodoItemDO.class)))
                    .thenAnswer(invocation -> {
                        TodoItemDO item = invocation.getArgument(0);
                        item.setId(2L);
                        return 1;
                    });

            // Act
            Long result = todoService.createTodo(request);

            // Assert
            assertThat(result).isEqualTo(2L);

            ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
            verify(todoItemMapper).insert(captor.capture());
            assertThat(captor.getValue().getDescription()).isNull();
        }

        @Test
        @DisplayName("异常路径：名称为空，抛出TODO_001异常")
        void should_throwException_when_titleIsNull() {
            // Arrange
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle(null);

            // Act & Assert
            assertThatThrownBy(() -> todoService.createTodo(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("事项名称不能为空")
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo("TODO_001"));
        }

        @Test
        @DisplayName("异常路径：名称为空白字符串，抛出TODO_001异常")
        void should_throwException_when_titleIsBlank() {
            // Arrange
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("   ");

            // Act & Assert
            assertThatThrownBy(() -> todoService.createTodo(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("事项名称不能为空")
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo("TODO_001"));
        }

        @Test
        @DisplayName("异常路径：名称超过100字符，抛出TODO_002异常")
        void should_throwException_when_titleExceedsMaxLength() {
            // Arrange
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("a".repeat(101));

            // Act & Assert
            assertThatThrownBy(() -> todoService.createTodo(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("事项名称长度超过限制（100字符）")
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo("TODO_002"));
        }

        @Test
        @DisplayName("异常路径：描述超过500字符，抛出TODO_003异常")
        void should_throwException_when_descriptionExceedsMaxLength() {
            // Arrange
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("有效标题");
            request.setDescription("d".repeat(501));

            // Act & Assert
            assertThatThrownBy(() -> todoService.createTodo(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("事项描述长度超过限制（500字符）")
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo("TODO_003"));
        }

        @Test
        @DisplayName("异常路径：数据库写入失败，抛出B0001异常")
        void should_throwException_when_insertFails() {
            // Arrange
            CreateTodoRequest request = new CreateTodoRequest();
            request.setTitle("有效任务");

            when(todoItemMapper.insert(any(TodoItemDO.class))).thenReturn(0);

            // Act & Assert
            assertThatThrownBy(() -> todoService.createTodo(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("系统繁忙，请稍后重试")
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo("B0001"));
        }
    }
}
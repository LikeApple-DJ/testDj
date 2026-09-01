package com.aiwork.todo.service.impl;

import com.aiwork.todo.common.exception.BizException;
import com.aiwork.todo.common.exception.TodoErrorCodeEnum;
import com.aiwork.todo.dao.mapper.TodoItemMapper;
import com.aiwork.todo.model.dto.CreateTodoRequest;
import com.aiwork.todo.model.dto.CreateTodoResult;
import com.aiwork.todo.model.entity.TodoItemDO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 待办事项业务服务实现单元测试
 *
 * <p>被测类：{@link TodoItemServiceImpl}；外部依赖 TodoItemMapper 使用 Mockito 注入。</p>
 *
 * @author AiWork
 * @date 2026/09/01
 */
@ExtendWith(MockitoExtension.class)
class TodoItemServiceImplTest {

    private static final Long GENERATED_ID = 1001L;

    @Mock
    private TodoItemMapper todoItemMapper;

    @InjectMocks
    private TodoItemServiceImpl todoItemService;

    // ==================== createTodoItem 测试 ====================

    @Test
    void should_returnId_when_requestIsValid() {
        // Arrange
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("完成周报");
        request.setDescription("本周工作总结，周五前提交");
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            ((TodoItemDO) invocation.getArgument(0)).setId(GENERATED_ID);
            return 1;
        });

        // Act
        CreateTodoResult result = todoItemService.createTodoItem(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).as("应返回生成的事项 ID").isEqualTo(GENERATED_ID);

        ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
        verify(todoItemMapper, times(1)).insert(captor.capture());
        TodoItemDO saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("完成周报");
        assertThat(saved.getDescription()).isEqualTo("本周工作总结，周五前提交");
        assertThat(saved.getStatus()).as("状态应为 PENDING").isEqualTo("PENDING");
        assertThat(saved.getTenantId()).isEqualTo("");
        assertThat(saved.getCreator()).isEqualTo("");
    }

    @Test
    void should_useEmptyString_when_descriptionIsNull() {
        // Arrange
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("取快递");
        request.setDescription(null);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            ((TodoItemDO) invocation.getArgument(0)).setId(GENERATED_ID);
            return 1;
        });

        // Act
        CreateTodoResult result = todoItemService.createTodoItem(request);

        // Assert
        assertThat(result.getId()).isEqualTo(GENERATED_ID);
        ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
        verify(todoItemMapper).insert(captor.capture());
        assertThat(captor.getValue().getDescription()).as("描述为空时存空串").isEqualTo("");
    }

    @Test
    void should_createSuccess_when_titleAndDescriptionAtMaxLength() {
        // Arrange - 边界值：title 正好 100 字符，description 正好 1000 字符
        String maxTitle = "a".repeat(100);
        String maxDescription = "b".repeat(1000);
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle(maxTitle);
        request.setDescription(maxDescription);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            ((TodoItemDO) invocation.getArgument(0)).setId(GENERATED_ID);
            return 1;
        });

        // Act
        CreateTodoResult result = todoItemService.createTodoItem(request);

        // Assert
        assertThat(result.getId()).as("边界长度应创建成功").isEqualTo(GENERATED_ID);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void should_throwTodo001_when_titleIsBlank(String title) {
        // Arrange
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle(title);
        request.setDescription("描述");

        // Act
        Throwable thrown = catchThrowable(() -> todoItemService.createTodoItem(request));

        // Assert
        assertThat(thrown).isInstanceOf(BizException.class);
        assertThat(((BizException) thrown).getErrorCode())
                .as("名称为空应返回 TODO_001")
                .isEqualTo(TodoErrorCodeEnum.TODO_001.getCode());
        verify(todoItemMapper, times(0)).insert(any(TodoItemDO.class));
    }

    @Test
    void should_throwTodo002_when_titleExceedsMaxLength() {
        // Arrange - title 101 字符
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("a".repeat(101));
        request.setDescription("描述");

        // Act
        Throwable thrown = catchThrowable(() -> todoItemService.createTodoItem(request));

        // Assert
        assertThat(thrown).isInstanceOf(BizException.class);
        assertThat(((BizException) thrown).getErrorCode())
                .as("名称超长应返回 TODO_002")
                .isEqualTo(TodoErrorCodeEnum.TODO_002.getCode());
        verify(todoItemMapper, times(0)).insert(any(TodoItemDO.class));
    }

    @Test
    void should_throwTodo003_when_descriptionExceedsMaxLength() {
        // Arrange - description 1001 字符
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("合法名称");
        request.setDescription("b".repeat(1001));

        // Act
        Throwable thrown = catchThrowable(() -> todoItemService.createTodoItem(request));

        // Assert
        assertThat(thrown).isInstanceOf(BizException.class);
        assertThat(((BizException) thrown).getErrorCode())
                .as("描述超长应返回 TODO_003")
                .isEqualTo(TodoErrorCodeEnum.TODO_003.getCode());
        verify(todoItemMapper, times(0)).insert(any(TodoItemDO.class));
    }

    @Test
    void should_throwTodo999_when_mapperThrowsException() {
        // Arrange - 数据库写入异常
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("合法名称");
        request.setDescription("描述");
        when(todoItemMapper.insert(any(TodoItemDO.class)))
                .thenThrow(new org.springframework.dao.DataAccessResourceFailureException("db down"));

        // Act
        Throwable thrown = catchThrowable(() -> todoItemService.createTodoItem(request));

        // Assert
        assertThat(thrown).isInstanceOf(BizException.class);
        assertThat(((BizException) thrown).getErrorCode())
                .as("数据库异常应返回 TODO_999")
                .isEqualTo(TodoErrorCodeEnum.TODO_999.getCode());
        verify(todoItemMapper, times(1)).insert(any(TodoItemDO.class));
    }

    @Test
    void should_throwTodo999_when_insertAffectsNoRow() {
        // Arrange - 受影响行数为 0
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("合法名称");
        request.setDescription("描述");
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenReturn(0);

        // Act
        Throwable thrown = catchThrowable(() -> todoItemService.createTodoItem(request));

        // Assert
        assertThat(thrown).isInstanceOf(BizException.class);
        assertThat(((BizException) thrown).getErrorCode())
                .as("影响行数异常应返回 TODO_999")
                .isEqualTo(TodoErrorCodeEnum.TODO_999.getCode());
        verify(todoItemMapper, times(1)).insert(any(TodoItemDO.class));
    }
}

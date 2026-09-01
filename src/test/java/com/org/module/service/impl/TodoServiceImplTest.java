package com.org.module.service.impl;

import com.org.module.context.UserContext;
import com.org.module.dto.TodoDTO;
import com.org.module.entity.Todo;
import com.org.module.exception.BusinessException;
import com.org.module.mapper.TodoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TodoServiceImpl} 单元测试
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    private static final Long CREATOR_ID = 1001L;

    @Mock
    private UserContext userContext;

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    @Captor
    private ArgumentCaptor<Todo> todoCaptor;

    // ==================== createTodo 测试 ====================

    @Test
    void should_createTodo_when_creatorIdPresent() {
        // Arrange
        TodoDTO dto = buildDto("完成季度汇报", "整理 Q3 数据并提交评审");
        when(userContext.getCurrentUserId()).thenReturn(Optional.of(CREATOR_ID));
        when(todoMapper.insert(any(Todo.class))).thenReturn(1);

        // Act
        todoService.createTodo(dto);

        // Assert - 验证落库参数由登录上下文注入 creator_id
        verify(todoMapper, times(1)).insert(todoCaptor.capture());
        Todo saved = todoCaptor.getValue();
        assertThat(saved.getTitle()).isEqualTo("完成季度汇报");
        assertThat(saved.getDescription()).isEqualTo("整理 Q3 数据并提交评审");
        assertThat(saved.getCreatorId()).as("creator_id 应来自登录上下文").isEqualTo(CREATOR_ID);
    }

    @Test
    void should_createTodo_when_descriptionIsNull() {
        // Arrange - 描述选填，允许为空
        TodoDTO dto = buildDto("提醒开会", null);
        when(userContext.getCurrentUserId()).thenReturn(Optional.of(CREATOR_ID));
        when(todoMapper.insert(any(Todo.class))).thenReturn(1);

        // Act
        todoService.createTodo(dto);

        // Assert
        verify(todoMapper, times(1)).insert(todoCaptor.capture());
        assertThat(todoCaptor.getValue().getDescription()).isNull();
        assertThat(todoCaptor.getValue().getCreatorId()).isEqualTo(CREATOR_ID);
    }

    @Test
    void should_throwBusinessException_when_creatorIdAbsent() {
        // Arrange - 登录上下文缺失
        TodoDTO dto = buildDto("完成季度汇报", null);
        when(userContext.getCurrentUserId()).thenReturn(Optional.empty());

        // Act & Assert - R03：未获取到登录用户信息，抛 TODO_003
        assertThatThrownBy(() -> todoService.createTodo(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("未获取到登录用户信息");
        verify(todoMapper, never()).insert(any(Todo.class));
    }

    private TodoDTO buildDto(String title, String description) {
        TodoDTO dto = new TodoDTO();
        dto.setTitle(title);
        dto.setDescription(description);
        return dto;
    }
}

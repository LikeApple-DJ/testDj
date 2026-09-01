package com.org.module.service.impl;

import com.org.module.dto.TodoDTO;
import com.org.module.dto.TodoVO;
import com.org.module.entity.Todo;
import com.org.module.exception.BusinessException;
import com.org.module.mapper.TodoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TodoServiceImpl} 单元测试
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    // ==================== createTodo 测试 ====================

    @Test
    void should_returnTodoVO_when_requestIsValid() {
        // Arrange
        TodoDTO dto = new TodoDTO();
        dto.setTitle("完成周报");
        dto.setDescription("本周工作总结与下周计划");
        when(todoMapper.insert(any(Todo.class))).thenAnswer(invocation -> {
            Todo saved = invocation.getArgument(0);
            saved.setId(1L);
            return 1;
        });

        // Act
        TodoVO vo = todoService.createTodo(dto);

        // Assert
        assertThat(vo).isNotNull();
        assertThat(vo.getId()).as("新建事项应返回主键").isEqualTo(1L);
        assertThat(vo.getTitle()).isEqualTo("完成周报");
        assertThat(vo.getDescription()).isEqualTo("本周工作总结与下周计划");
        assertThat(vo.getStatus()).as("新建事项状态应为待处理").isZero();
        verify(todoMapper).insert(any(Todo.class));
    }

    @Test
    void should_saveTodo_when_descriptionIsNull() {
        // Arrange
        TodoDTO dto = new TodoDTO();
        dto.setTitle("取快递");
        dto.setDescription(null);
        when(todoMapper.insert(any(Todo.class))).thenAnswer(invocation -> {
            Todo saved = invocation.getArgument(0);
            saved.setId(2L);
            return 1;
        });

        // Act
        TodoVO vo = todoService.createTodo(dto);

        // Assert
        assertThat(vo.getId()).isEqualTo(2L);
        assertThat(vo.getTitle()).isEqualTo("取快递");
        assertThat(vo.getDescription()).isNull();
        assertThat(vo.getStatus()).isZero();
    }

    @Test
    void should_throwBusinessException_when_saveFails() {
        // Arrange
        TodoDTO dto = new TodoDTO();
        dto.setTitle("失败事项");
        when(todoMapper.insert(any(Todo.class))).thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("待办事项创建失败");
    }
}

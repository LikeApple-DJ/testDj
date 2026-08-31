package com.org.module.service.impl;

import com.org.module.dto.TodoCreateRequest;
import com.org.module.entity.TodoItem;
import com.org.module.mapper.TodoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 待办事项服务单元测试。
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    // ==================== createTodo 测试 ====================

    @Test
    void should_returnId_and_persistFields_when_createValidTodo() {
        // Arrange
        TodoCreateRequest request = new TodoCreateRequest();
        request.setName("完成季度汇报");
        request.setDescription("整理 Q3 数据并提交评审");
        // 模拟数据库自增主键回填
        when(todoMapper.insert(any(TodoItem.class))).thenAnswer(invocation -> {
            invocation.<TodoItem>getArgument(0).setId(1001L);
            return 1;
        });

        // Act
        Long id = todoService.createTodo(request, "u100");

        // Assert - 返回主键
        assertThat(id).as("应返回新建主键").isEqualTo(1001L);
        // Assert - 回查落库实体字段
        ArgumentCaptor<TodoItem> captor = ArgumentCaptor.forClass(TodoItem.class);
        verify(todoMapper).insert(captor.capture());
        TodoItem saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("完成季度汇报");
        assertThat(saved.getDescription()).isEqualTo("整理 Q3 数据并提交评审");
        assertThat(saved.getCreator()).isEqualTo("u100");
        assertThat(saved.getIsDeleted()).as("逻辑删除标记初始为 0").isZero();
        assertThat(saved.getGmtCreate()).as("创建时间不应为空").isNotNull();
        assertThat(saved.getGmtModified()).as("修改时间不应为空").isNotNull();
    }

    @Test
    void should_setCreatorNull_when_xUserIdHeaderMissing() {
        // Arrange
        TodoCreateRequest request = new TodoCreateRequest();
        request.setName("写文档");
        when(todoMapper.insert(any(TodoItem.class))).thenAnswer(invocation -> {
            invocation.<TodoItem>getArgument(0).setId(2L);
            return 1;
        });

        // Act
        Long id = todoService.createTodo(request, null);

        // Assert - 请求头缺失时 creator 落 NULL，不阻断创建（见 A01/R03）
        assertThat(id).isEqualTo(2L);
        ArgumentCaptor<TodoItem> captor = ArgumentCaptor.forClass(TodoItem.class);
        verify(todoMapper).insert(captor.capture());
        assertThat(captor.getValue().getCreator()).isNull();
    }

    @Test
    void should_persist_when_descriptionIsNull() {
        // Arrange
        TodoCreateRequest request = new TodoCreateRequest();
        request.setName("仅名称");
        when(todoMapper.insert(any(TodoItem.class))).thenAnswer(invocation -> {
            invocation.<TodoItem>getArgument(0).setId(3L);
            return 1;
        });

        // Act
        Long id = todoService.createTodo(request, "u1");

        // Assert - 描述可选，为空时正常落库
        assertThat(id).isEqualTo(3L);
        ArgumentCaptor<TodoItem> captor = ArgumentCaptor.forClass(TodoItem.class);
        verify(todoMapper).insert(captor.capture());
        assertThat(captor.getValue().getDescription()).isNull();
    }
}

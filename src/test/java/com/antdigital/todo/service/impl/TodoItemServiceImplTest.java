package com.antdigital.todo.service.impl;

import com.antdigital.todo.common.constant.TodoConstants;
import com.antdigital.todo.common.exception.BusinessException;
import com.antdigital.todo.dao.mapper.TodoItemMapper;
import com.antdigital.todo.model.dto.TodoItemCreateRequest;
import com.antdigital.todo.model.entity.TodoItemDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TodoItemServiceImpl} 单元测试
 * <p>
 * 遵循 AAA 模式，覆盖正常路径、参数校验、边界值、异常路径。
 * </p>
 *
 * @author AiWork
 * @date 2026/08/31
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("待办事项 Service 单元测试")
class TodoItemServiceImplTest {

    private static final Long TEST_ID = 1001L;
    private static final String VALID_TITLE = "完成季度汇报材料";
    private static final String VALID_DESCRIPTION = "整理Q3数据并准备PPT";
    private static final String CREATOR = "test-user";

    @Mock
    private TodoItemMapper todoItemMapper;

    @InjectMocks
    private TodoItemServiceImpl todoItemService;

    @BeforeEach
    void setUp() {
        // 注入 @Value 字段（模拟配置注入）
        ReflectionTestUtils.setField(todoItemService, "creator", CREATOR);
    }

    // ==================== createTodoItem 正常路径 ====================

    @Test
    @DisplayName("正常创建待办事项，返回事项ID")
    void should_returnId_when_validRequest() {
        // Arrange (Given)
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle(VALID_TITLE);
        request.setDescription(VALID_DESCRIPTION);

        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            TodoItemDO arg = invocation.getArgument(0);
            arg.setId(TEST_ID);
            return 1;
        });

        // Act (When)
        Long result = todoItemService.createTodoItem(request);

        // Assert (Then)
        assertThat(result).isEqualTo(TEST_ID);

        // 验证传入 Mapper 的数据正确
        ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
        verify(todoItemMapper, times(1)).insert(captor.capture());
        TodoItemDO saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo(VALID_TITLE);
        assertThat(saved.getDescription()).isEqualTo(VALID_DESCRIPTION);
        assertThat(saved.getCreator()).isEqualTo(CREATOR);
        assertThat(saved.getGmtCreate()).isNotNull();
        assertThat(saved.getGmtModified()).isNotNull();
    }

    @Test
    @DisplayName("描述为空时正常创建")
    void should_returnId_when_descriptionIsNull() {
        // Arrange
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle(VALID_TITLE);
        request.setDescription(null);

        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            invocation.<TodoItemDO>getArgument(0).setId(TEST_ID);
            return 1;
        });

        // Act
        Long result = todoItemService.createTodoItem(request);

        // Assert
        assertThat(result).isEqualTo(TEST_ID);
        ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
        verify(todoItemMapper).insert(captor.capture());
        assertThat(captor.getValue().getDescription()).isNull();
    }

    // ==================== 参数校验 ====================

    @Test
    @DisplayName("请求对象为 null 时抛出业务异常")
    void should_throwException_when_requestIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodoItem(null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(TodoConstants.MSG_TITLE_EMPTY);

        // 验证未调用 Mapper
        verify(todoItemMapper, never()).insert(any());
    }

    @Test
    @DisplayName("事项名称为空时抛出 TODO_001")
    void should_throwException_when_titleIsBlank() {
        // Arrange
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle("");
        request.setDescription(VALID_DESCRIPTION);

        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodoItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(TodoConstants.MSG_TITLE_EMPTY);

        verify(todoItemMapper, never()).insert(any());
    }

    @Test
    @DisplayName("事项名称为空白字符时抛出 TODO_001")
    void should_throwException_when_titleIsWhitespace() {
        // Arrange
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle("   ");
        request.setDescription(VALID_DESCRIPTION);

        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodoItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(TodoConstants.MSG_TITLE_EMPTY);

        verify(todoItemMapper, never()).insert(any());
    }

    @Test
    @DisplayName("事项名称超过100字符时抛出 TODO_002")
    void should_throwException_when_titleExceedsMaxLength() {
        // Arrange
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle("a".repeat(TodoConstants.TITLE_MAX_LENGTH + 1));
        request.setDescription(VALID_DESCRIPTION);

        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodoItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(TodoConstants.MSG_TITLE_TOO_LONG);

        verify(todoItemMapper, never()).insert(any());
    }

    @Test
    @DisplayName("事项描述超过500字符时抛出 TODO_003")
    void should_throwException_when_descriptionExceedsMaxLength() {
        // Arrange
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle(VALID_TITLE);
        request.setDescription("a".repeat(TodoConstants.DESCRIPTION_MAX_LENGTH + 1));

        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodoItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(TodoConstants.MSG_DESCRIPTION_TOO_LONG);

        verify(todoItemMapper, never()).insert(any());
    }

    // ==================== 边界值 ====================

    @Test
    @DisplayName("事项名称恰好100字符时正常创建")
    void should_returnId_when_titleIsExactlyMaxLength() {
        // Arrange
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle("a".repeat(TodoConstants.TITLE_MAX_LENGTH));
        request.setDescription(VALID_DESCRIPTION);

        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            invocation.<TodoItemDO>getArgument(0).setId(TEST_ID);
            return 1;
        });

        // Act
        Long result = todoItemService.createTodoItem(request);

        // Assert
        assertThat(result).isEqualTo(TEST_ID);
    }

    @Test
    @DisplayName("事项描述恰好500字符时正常创建")
    void should_returnId_when_descriptionIsExactlyMaxLength() {
        // Arrange
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle(VALID_TITLE);
        request.setDescription("a".repeat(TodoConstants.DESCRIPTION_MAX_LENGTH));

        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            invocation.<TodoItemDO>getArgument(0).setId(TEST_ID);
            return 1;
        });

        // Act
        Long result = todoItemService.createTodoItem(request);

        // Assert
        assertThat(result).isEqualTo(TEST_ID);
    }

    @Test
    @DisplayName("事项名称恰好1字符时正常创建")
    void should_returnId_when_titleIsSingleChar() {
        // Arrange
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle("a");
        request.setDescription(null);

        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            invocation.<TodoItemDO>getArgument(0).setId(TEST_ID);
            return 1;
        });

        // Act
        Long result = todoItemService.createTodoItem(request);

        // Assert
        assertThat(result).isEqualTo(TEST_ID);
    }

    // ==================== 异常路径 ====================

    @Test
    @DisplayName("创建人标识为空时抛出 TODO_999")
    void should_throwException_when_creatorIsBlank() {
        // Arrange
        ReflectionTestUtils.setField(todoItemService, "creator", "");
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle(VALID_TITLE);
        request.setDescription(VALID_DESCRIPTION);

        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodoItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(TodoConstants.MSG_CREATOR_MISSING);

        verify(todoItemMapper, never()).insert(any());
    }

    @Test
    @DisplayName("Mapper 写入抛出异常时包装为 TODO_999")
    void should_throwBusinessException_when_mapperThrows() {
        // Arrange
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle(VALID_TITLE);
        request.setDescription(VALID_DESCRIPTION);

        when(todoItemMapper.insert(any(TodoItemDO.class)))
                .thenThrow(new RuntimeException("数据库连接失败"));

        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodoItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(TodoConstants.MSG_SYSTEM_ERROR);

        verify(todoItemMapper, times(1)).insert(any());
    }

    @Test
    @DisplayName("Mapper 返回0行时抛出 TODO_999")
    void should_throwBusinessException_when_insertAffectsZeroRows() {
        // Arrange
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle(VALID_TITLE);
        request.setDescription(VALID_DESCRIPTION);

        when(todoItemMapper.insert(any(TodoItemDO.class))).thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodoItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(TodoConstants.MSG_SYSTEM_ERROR);
    }
}

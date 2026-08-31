package com.antdigital.todo.todo.service.impl;

import com.antdigital.todo.common.constant.TodoConstants;
import com.antdigital.todo.common.exception.BusinessException;
import com.antdigital.todo.todo.enums.TodoErrorCodeEnum;
import com.antdigital.todo.todo.model.dto.TodoCreateRequest;
import com.antdigital.todo.todo.model.dto.TodoCreateResult;
import com.antdigital.todo.todo.model.entity.TodoDO;
import com.antdigital.todo.todo.repository.TodoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TodoServiceImpl} 单元测试
 *
 * @author AiWork
 * @date 2026/08/31
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    // ==================== createTodo 测试 ====================

    @Test
    @DisplayName("正常路径：合法名称与描述，持久化成功返回 ID")
    void should_returnResultWithId_when_validRequest() {
        // Arrange
        TodoCreateRequest request = buildRequest("完成系分评审", "本周五前完成待办模块系分文档评审");
        TodoDO savedTodo = new TodoDO();
        savedTodo.setId(1001L);
        savedTodo.setName("完成系分评审");
        savedTodo.setDescription("本周五前完成待办模块系分文档评审");
        when(todoRepository.save(any(TodoDO.class))).thenReturn(savedTodo);

        // Act
        TodoCreateResult result = todoService.createTodo(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1001L);
        verify(todoRepository, times(1)).save(any(TodoDO.class));
    }

    @Test
    @DisplayName("正常路径：description 为 null 时仍可创建")
    void should_returnResultWithId_when_descriptionIsNull() {
        // Arrange
        TodoCreateRequest request = buildRequest("买牛奶", null);
        TodoDO savedTodo = new TodoDO();
        savedTodo.setId(1002L);
        savedTodo.setName("买牛奶");
        savedTodo.setDescription(null);
        when(todoRepository.save(any(TodoDO.class))).thenReturn(savedTodo);

        // Act
        TodoCreateResult result = todoService.createTodo(request);

        // Assert
        assertThat(result.getId()).isEqualTo(1002L);
    }

    @Test
    @DisplayName("正常路径：name 前后空白被 trim，tenant_id 注入默认值")
    void should_trimNameAndInjectDefaultTenant_when_validRequest() {
        // Arrange
        TodoCreateRequest request = buildRequest("  带空格的名称  ", "描述");
        TodoDO savedTodo = new TodoDO();
        savedTodo.setId(1003L);
        savedTodo.setName("带空格的名称");
        when(todoRepository.save(any(TodoDO.class))).thenReturn(savedTodo);

        // Act
        todoService.createTodo(request);

        // Assert - 通过 ArgumentCaptor 验证传入 Repository 的实体字段
        ArgumentCaptor<TodoDO> captor = ArgumentCaptor.forClass(TodoDO.class);
        verify(todoRepository).save(captor.capture());
        TodoDO captured = captor.getValue();
        assertThat(captured.getName()).isEqualTo("带空格的名称");
        assertThat(captured.getTenantId()).isEqualTo(TodoConstants.DEFAULT_TENANT_ID);
        assertThat(captured.getDescription()).isEqualTo("描述");
    }

    @Test
    @DisplayName("R01：name 为 null 时抛 TODO_0001")
    void should_throwBusinessException_when_nameIsNull() {
        // Arrange
        TodoCreateRequest request = buildRequest(null, "描述");

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(TodoErrorCodeEnum.NAME_BLANK.getErrorMessage());
    }

    @Test
    @DisplayName("R01：name 全空白时抛 TODO_0001")
    void should_throwBusinessException_when_nameIsAllWhitespace() {
        // Arrange
        TodoCreateRequest request = buildRequest("   ", "描述");

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(TodoErrorCodeEnum.NAME_BLANK.getErrorMessage());
    }

    @Test
    @DisplayName("R02：name 超过 100 字符时抛 TODO_0002")
    void should_throwBusinessException_when_nameTooLong() {
        // Arrange
        String longName = "a".repeat(TodoConstants.MAX_NAME_LENGTH + 1);
        TodoCreateRequest request = buildRequest(longName, "描述");

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(TodoErrorCodeEnum.NAME_TOO_LONG.getErrorMessage());
    }

    @Test
    @DisplayName("R03：description 超过 500 字符时抛 TODO_0003")
    void should_throwBusinessException_when_descriptionTooLong() {
        // Arrange
        String longDescription = "d".repeat(TodoConstants.MAX_DESCRIPTION_LENGTH + 1);
        TodoCreateRequest request = buildRequest("合法名称", longDescription);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(TodoErrorCodeEnum.DESCRIPTION_TOO_LONG.getErrorMessage());
    }

    @Test
    @DisplayName("边界值：name 恰好 100 字符可正常创建")
    void should_createSuccessfully_when_nameIsMaxLength() {
        // Arrange
        String maxLengthName = "a".repeat(TodoConstants.MAX_NAME_LENGTH);
        TodoCreateRequest request = buildRequest(maxLengthName, null);
        TodoDO savedTodo = new TodoDO();
        savedTodo.setId(1004L);
        savedTodo.setName(maxLengthName);
        when(todoRepository.save(any(TodoDO.class))).thenReturn(savedTodo);

        // Act
        TodoCreateResult result = todoService.createTodo(request);

        // Assert
        assertThat(result.getId()).isEqualTo(1004L);
    }

    @Test
    @DisplayName("边界值：description 恰好 500 字符可正常创建")
    void should_createSuccessfully_when_descriptionIsMaxLength() {
        // Arrange
        String maxLengthDesc = "d".repeat(TodoConstants.MAX_DESCRIPTION_LENGTH);
        TodoCreateRequest request = buildRequest("名称", maxLengthDesc);
        TodoDO savedTodo = new TodoDO();
        savedTodo.setId(1005L);
        when(todoRepository.save(any(TodoDO.class))).thenReturn(savedTodo);

        // Act
        TodoCreateResult result = todoService.createTodo(request);

        // Assert
        assertThat(result.getId()).isEqualTo(1005L);
    }

    @Test
    @DisplayName("异常路径：Repository 抛 DataAccessException 时抛 TODO_0004")
    void should_throwBusinessException_when_repositoryThrowsDataAccessException() {
        // Arrange
        TodoCreateRequest request = buildRequest("合法名称", "描述");
        DataAccessException dbEx = new QueryTimeoutException("数据库连接超时");
        when(todoRepository.save(any(TodoDO.class))).thenThrow(dbEx);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(TodoErrorCodeEnum.SYSTEM_ERROR.getErrorMessage());
    }

    /**
     * 构造测试用创建请求
     *
     * @param name        事项名称
     * @param description 事项描述
     * @return 创建请求
     */
    private TodoCreateRequest buildRequest(String name, String description) {
        TodoCreateRequest request = new TodoCreateRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }
}

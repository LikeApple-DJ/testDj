package com.antdigital.todo.service.impl;

import com.antdigital.todo.common.BizException;
import com.antdigital.todo.common.ErrorCode;
import com.antdigital.todo.common.UserContext;
import com.antdigital.todo.dao.mapper.TodoMapper;
import com.antdigital.todo.enums.IsDeleted;
import com.antdigital.todo.enums.TodoStatus;
import com.antdigital.todo.model.dto.TodoCreateRequest;
import com.antdigital.todo.model.dto.TodoCreateResponse;
import com.antdigital.todo.model.entity.TodoDO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.time.Clock;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TodoServiceImpl 单元测试。
 *
 * <p>遵循 AAA 模式，使用 Mockito + AssertJ。
 * 被测类: {@link TodoServiceImpl}，依赖: {@link TodoMapper}（Mock）。</p>
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    private static final String TENANT_ID = "tenant_001";
    private static final String CREATOR = "user_zhangsan";
    private static final Long GENERATED_ID = 1001L;

    @Mock
    private TodoMapper todoMapper;

    private TodoServiceImpl todoService;

    @BeforeEach
    void setUp() {
        // 注入登录态上下文（模拟拦截器已完成登录校验）
        UserContext.set(TENANT_ID, CREATOR);
        // 使用真实 Clock（Asia/Shanghai），与 TimeConfig Bean 一致
        todoService = new TodoServiceImpl(todoMapper, Clock.system(ZoneId.of("Asia/Shanghai")));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    // ==================== createTodo 正常路径 ====================

    @Test
    @DisplayName("正常路径：合法请求创建成功，返回新建ID")
    void should_returnId_when_validRequest() {
        // Arrange (Given)
        TodoCreateRequest request = buildRequest("完成周报", "本周五前提交部门周报");
        when(todoMapper.selectByTenantAndName(eq(TENANT_ID), eq("完成周报"))).thenReturn(null);
        // 模拟 insert 回写自增主键
        when(todoMapper.insert(any(TodoDO.class))).thenAnswer(invocation -> {
            ((TodoDO) invocation.getArgument(0)).setId(GENERATED_ID);
            return 1;
        });

        // Act (When)
        TodoCreateResponse response = todoService.createTodo(request);

        // Assert (Then)
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(GENERATED_ID);
    }

    @Test
    @DisplayName("正常路径：description 为 null 时也能正常创建")
    void should_returnId_when_descriptionIsNull() {
        // Arrange
        TodoCreateRequest request = buildRequest("买咖啡", null);
        when(todoMapper.selectByTenantAndName(eq(TENANT_ID), eq("买咖啡"))).thenReturn(null);
        when(todoMapper.insert(any(TodoDO.class))).thenAnswer(invocation -> {
            ((TodoDO) invocation.getArgument(0)).setId(GENERATED_ID);
            return 1;
        });

        // Act
        TodoCreateResponse response = todoService.createTodo(request);

        // Assert
        assertThat(response.getId()).isEqualTo(GENERATED_ID);
    }

    // ==================== R04: 登录态校验 ====================

    @Test
    @DisplayName("R04: 未登录时抛出 TODO_005")
    void should_throwTodo005_when_notLoggedIn() {
        // Arrange — 清除登录态
        UserContext.clear();
        TodoCreateRequest request = buildRequest("完成周报", "描述");

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BizException.class)
                .satisfies(ex -> {
                    BizException bizEx = (BizException) ex;
                    assertThat(bizEx.getErrorCode()).isEqualTo(ErrorCode.TODO_005);
                });

        // 验证未执行查询和插入
        verify(todoMapper, never()).selectByTenantAndName(anyString(), anyString());
        verify(todoMapper, never()).insert(any(TodoDO.class));
    }

    // ==================== R01: 名称校验 ====================

    @Test
    @DisplayName("R01: name 为空时抛出 TODO_001")
    void should_throwTodo001_when_nameIsBlank() {
        // Arrange
        TodoCreateRequest request = buildRequest("", "描述");

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BizException.class)
                .satisfies(ex -> assertThat(((BizException) ex).getErrorCode()).isEqualTo(ErrorCode.TODO_001));

        verify(todoMapper, never()).selectByTenantAndName(anyString(), anyString());
    }

    @Test
    @DisplayName("R01: name 为 null 时抛出 TODO_001")
    void should_throwTodo001_when_nameIsNull() {
        // Arrange
        TodoCreateRequest request = buildRequest(null, "描述");

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BizException.class)
                .satisfies(ex -> assertThat(((BizException) ex).getErrorCode()).isEqualTo(ErrorCode.TODO_001));
    }

    @Test
    @DisplayName("R02: name 超过128字符时抛出 TODO_002")
    void should_throwTodo002_when_nameExceeds128() {
        // Arrange — 129 个字符
        String longName = "a".repeat(129);
        TodoCreateRequest request = buildRequest(longName, "描述");

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BizException.class)
                .satisfies(ex -> assertThat(((BizException) ex).getErrorCode()).isEqualTo(ErrorCode.TODO_002));
    }

    // ==================== R02: 描述长度校验 ====================

    @Test
    @DisplayName("R02: description 超过1024字符时抛出 TODO_003")
    void should_throwTodo003_when_descriptionExceeds1024() {
        // Arrange — 1025 个字符
        String longDescription = "b".repeat(1025);
        TodoCreateRequest request = buildRequest("完成周报", longDescription);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BizException.class)
                .satisfies(ex -> assertThat(((BizException) ex).getErrorCode()).isEqualTo(ErrorCode.TODO_003));

        verify(todoMapper, never()).selectByTenantAndName(anyString(), anyString());
    }

    // ==================== R03: 唯一性校验 ====================

    @Test
    @DisplayName("R03: 同租户同名已存在时抛出 TODO_004")
    void should_throwTodo004_when_nameAlreadyExists() {
        // Arrange
        TodoCreateRequest request = buildRequest("完成周报", "描述");
        TodoDO existing = new TodoDO();
        existing.setId(999L);
        existing.setTenantId(TENANT_ID);
        existing.setName("完成周报");
        when(todoMapper.selectByTenantAndName(eq(TENANT_ID), eq("完成周报"))).thenReturn(existing);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BizException.class)
                .satisfies(ex -> assertThat(((BizException) ex).getErrorCode()).isEqualTo(ErrorCode.TODO_004));

        // 验证未执行插入
        verify(todoMapper, never()).insert(any(TodoDO.class));
    }

    @Test
    @DisplayName("R03: 并发同名校验穿透（DuplicateKeyException）时抛出 TODO_004")
    void should_throwTodo004_when_concurrentDuplicateKey() {
        // Arrange — 预校验通过，但 insert 时唯一索引冲突
        TodoCreateRequest request = buildRequest("完成周报", "描述");
        when(todoMapper.selectByTenantAndName(eq(TENANT_ID), eq("完成周报"))).thenReturn(null);
        when(todoMapper.insert(any(TodoDO.class)))
                .thenThrow(new DuplicateKeyException("Duplicate entry"));

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BizException.class)
                .satisfies(ex -> assertThat(((BizException) ex).getErrorCode()).isEqualTo(ErrorCode.TODO_004));
    }

    // ==================== R05: 默认值覆盖 ====================

    @Test
    @DisplayName("R05: 创建时 status 默认0、deleted 默认0、tenant_id/creator 由登录态注入")
    void should_setDefaults_when_createTodo() {
        // Arrange
        TodoCreateRequest request = buildRequest("完成周报", "本周五前提交部门周报");
        when(todoMapper.selectByTenantAndName(eq(TENANT_ID), eq("完成周报"))).thenReturn(null);
        when(todoMapper.insert(any(TodoDO.class))).thenAnswer(invocation -> {
            ((TodoDO) invocation.getArgument(0)).setId(GENERATED_ID);
            return 1;
        });

        // Act
        todoService.createTodo(request);

        // Assert — 通过 ArgumentCaptor 验证传入 Mapper 的数据对象
        ArgumentCaptor<TodoDO> captor = ArgumentCaptor.forClass(TodoDO.class);
        verify(todoMapper).insert(captor.capture());
        TodoDO captured = captor.getValue();

        assertThat(captured.getTenantId()).isEqualTo(TENANT_ID);
        assertThat(captured.getCreator()).isEqualTo(CREATOR);
        assertThat(captured.getName()).isEqualTo("完成周报");
        assertThat(captured.getDescription()).isEqualTo("本周五前提交部门周报");
        assertThat(captured.getStatus()).isEqualTo(TodoStatus.PENDING.getCode());
        assertThat(captured.getDeleted()).isEqualTo(IsDeleted.NOT_DELETED.getCode());
        assertThat(captured.getGmtCreate()).isNotNull();
        assertThat(captured.getGmtModified()).isNotNull();
    }

    // ==================== 测试数据构造 ====================

    /**
     * 构建 TodoCreateRequest 测试数据。
     *
     * @param name        事项名称
     * @param description 事项描述
     * @return 请求对象
     */
    private static TodoCreateRequest buildRequest(String name, String description) {
        TodoCreateRequest request = new TodoCreateRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }
}

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
import com.antdigital.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * 待办事项业务服务实现。
 *
 * <p>对应 design.md §5.1.3.1 F01 新增待办事项。</p>
 */
@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    /** 事项名称最大长度 */
    private static final int NAME_MAX_LENGTH = 128;

    /** 事项描述最大长度 */
    private static final int DESCRIPTION_MAX_LENGTH = 1024;

    private final TodoMapper todoMapper;

    /** 统一时区 Clock，避免系统默认时区导致多实例/JVM-DB 时区不一致 */
    private final Clock clock;

    public TodoServiceImpl(TodoMapper todoMapper, Clock clock) {
        this.todoMapper = todoMapper;
        this.clock = clock;
    }

    @Override
    public TodoCreateResponse createTodo(TodoCreateRequest request) {
        // R04: 登录态校验 — tenant_id 和 creator 由服务端从登录态注入
        String tenantId = UserContext.getTenantId();
        String creator = UserContext.getCreator();
        if (tenantId == null || creator == null) {
            throw new BizException(ErrorCode.TODO_005);
        }

        // R01: name 非空，长度1-128（Controller 层 @Valid 前置，此处兜底）
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BizException(ErrorCode.TODO_001);
        }
        if (request.getName().length() > NAME_MAX_LENGTH) {
            throw new BizException(ErrorCode.TODO_002);
        }

        // R02: description 长度0-1024
        if (request.getDescription() != null && request.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
            throw new BizException(ErrorCode.TODO_003);
        }

        // R03: 同租户未删除范围内 name 唯一（预校验，并发由唯一索引兜底）
        TodoDO existing = todoMapper.selectByTenantAndName(tenantId, request.getName());
        if (existing != null) {
            throw new BizException(ErrorCode.TODO_004);
        }

        // R05: status 默认0、is_deleted 默认0，服务端强制覆盖入参
        TodoDO todo = new TodoDO();
        todo.setTenantId(tenantId);
        todo.setName(request.getName());
        todo.setDescription(request.getDescription());
        todo.setStatus(TodoStatus.PENDING.getCode());
        todo.setCreator(creator);
        todo.setDeleted(IsDeleted.NOT_DELETED.getCode());
        LocalDateTime now = LocalDateTime.now(clock);
        todo.setGmtCreate(now);
        todo.setGmtModified(now);

        // 插入，唯一索引兜底并发穿透
        try {
            todoMapper.insert(todo);
        } catch (DuplicateKeyException e) {
            logger.warn("并发同名校验穿透, tenantId: {}, name: {}, 返回 TODO_004", tenantId, request.getName());
            throw new BizException(ErrorCode.TODO_004);
        }

        logger.info("创建待办事项成功, tenantId: {}, creator: {}, id: {}", tenantId, creator, todo.getId());
        return new TodoCreateResponse(todo.getId());
    }
}

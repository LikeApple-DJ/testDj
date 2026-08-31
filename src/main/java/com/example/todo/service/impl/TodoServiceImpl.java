package com.example.todo.service.impl;

import com.example.todo.context.UserContext;
import com.example.todo.dto.TodoCreateRequest;
import com.example.todo.dto.TodoCreateResult;
import com.example.todo.exception.TodoErrorCode;
import com.example.todo.exception.TodoException;
import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import com.example.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 待办事项业务服务实现。
 *
 * <p>处理时序对应 design.md §5.2.3.1：设置 creatorId/tenantId → 落库 → 组装结果。
 * 落库失败统一映射为 TODO_0004 并记录 ERROR 日志含堆栈。</p>
 */
@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    /** 默认租户ID，当前单租户 */
    private static final Long DEFAULT_TENANT_ID = 0L;

    private final TodoRepository todoRepository;

    private final UserContext userContext;

    public TodoServiceImpl(TodoRepository todoRepository, UserContext userContext) {
        this.todoRepository = todoRepository;
        this.userContext = userContext;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoCreateResult createTodo(TodoCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Todo todo = new Todo();
        todo.setName(request.getName());
        todo.setDescription(request.getDescription());
        todo.setCreatorId(userContext.getCurrentUserId());
        todo.setTenantId(DEFAULT_TENANT_ID);
        todo.setGmtCreate(now);
        todo.setGmtModified(now);

        Todo saved;
        try {
            saved = todoRepository.save(todo);
        } catch (DataAccessException e) {
            logger.error("待办事项落库失败, name: {}, creatorId: {}, msg: {}",
                    request.getName(), todo.getCreatorId(), e.getMessage(), e);
            throw new TodoException(TodoErrorCode.SYSTEM_ERROR, e);
        }

        logger.info("待办事项创建成功, todoId: {}, creatorId: {}", saved.getId(), saved.getCreatorId());
        return toResult(saved);
    }

    /**
     * 将持久化实体转换为创建结果。
     *
     * @param todo 持久化后的实体
     * @return 创建结果
     */
    private TodoCreateResult toResult(Todo todo) {
        TodoCreateResult result = new TodoCreateResult();
        result.setId(todo.getId());
        result.setName(todo.getName());
        result.setDescription(todo.getDescription());
        result.setCreatorId(todo.getCreatorId());
        result.setGmtCreate(todo.getGmtCreate());
        return result;
    }
}

package com.aiwork.todo.service.impl;

import com.aiwork.todo.common.constant.TodoConstants;
import com.aiwork.todo.common.exception.BizException;
import com.aiwork.todo.common.exception.TodoErrorCodeEnum;
import com.aiwork.todo.dao.mapper.TodoItemMapper;
import com.aiwork.todo.model.dto.CreateTodoRequest;
import com.aiwork.todo.model.dto.CreateTodoResult;
import com.aiwork.todo.model.entity.TodoItemDO;
import com.aiwork.todo.model.enums.TodoStatusEnum;
import com.aiwork.todo.service.TodoItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 待办事项业务服务实现
 *
 * <p>业务规则：R01 名称非空、R02 名称≤100、R03 描述≤1000、R04 状态默认 PENDING、
 * R05 creator/tenant_id 由上下文带入，缺失时按默认值赋值不阻断创建。</p>
 *
 * @author AiWork
 * @date 2026/09/01
 */
@Service
public class TodoItemServiceImpl implements TodoItemService {

    private static final Logger logger = LoggerFactory.getLogger(TodoItemServiceImpl.class);

    private final TodoItemMapper todoItemMapper;

    public TodoItemServiceImpl(TodoItemMapper todoItemMapper) {
        this.todoItemMapper = todoItemMapper;
    }

    @Override
    public CreateTodoResult createTodoItem(CreateTodoRequest request) {
        // R01：校验事项名称非空
        String title = request.getTitle();
        if (!StringUtils.hasText(title)) {
            throw new BizException(TodoErrorCodeEnum.TODO_001);
        }

        // R02：校验事项名称长度
        if (title.length() > TodoConstants.MAX_TITLE_LENGTH) {
            throw new BizException(TodoErrorCodeEnum.TODO_002);
        }

        // 描述选填，为空时存空串，避免空值处理二义性
        String description = request.getDescription();
        if (description == null) {
            description = TodoConstants.DEFAULT_DESCRIPTION;
        }

        // R03：校验描述长度
        if (description.length() > TodoConstants.MAX_DESCRIPTION_LENGTH) {
            throw new BizException(TodoErrorCodeEnum.TODO_003);
        }

        // 构建数据对象（R04 状态默认 PENDING，R05 上下文缺失时写默认值）
        TodoItemDO todoItem = new TodoItemDO();
        todoItem.setTenantId(TodoConstants.DEFAULT_TENANT_ID);
        todoItem.setTitle(title);
        todoItem.setDescription(description);
        todoItem.setStatus(TodoStatusEnum.PENDING.getCode());
        todoItem.setCreator(TodoConstants.DEFAULT_CREATOR);

        // 上下文缺失 creator 时记录告警，不阻断创建
        logger.warn("createTodoItem creator missing, use default, title: {}", title);

        try {
            int affected = todoItemMapper.insert(todoItem);
            if (affected != 1) {
                logger.error("createTodoItem insert affected rows abnormal, affected: {}", affected);
                throw new BizException(TodoErrorCodeEnum.TODO_999);
            }
        } catch (BizException e) {
            throw e;
        } catch (DataAccessException e) {
            logger.error("createTodoItem data access error, title: {}", title, e);
            throw new BizException(TodoErrorCodeEnum.TODO_999);
        } catch (Exception e) {
            logger.error("createTodoItem system error, title: {}", title, e);
            throw new BizException(TodoErrorCodeEnum.TODO_999);
        }

        logger.info("createTodoItem success, id: {}", todoItem.getId());
        return new CreateTodoResult(todoItem.getId());
    }
}

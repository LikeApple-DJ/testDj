package com.antdigital.todo.todo.service.impl;

import com.antdigital.todo.common.constant.TodoConstants;
import com.antdigital.todo.common.exception.BusinessException;
import com.antdigital.todo.todo.enums.TodoErrorCodeEnum;
import com.antdigital.todo.todo.model.dto.TodoCreateRequest;
import com.antdigital.todo.todo.model.dto.TodoCreateResult;
import com.antdigital.todo.todo.model.entity.TodoDO;
import com.antdigital.todo.todo.repository.TodoRepository;
import com.antdigital.todo.todo.service.TodoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 待办事项业务服务实现类
 *
 * @author AiWork
 * @date 2026/08/31
 */
@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoCreateResult createTodo(TodoCreateRequest request) {
        // 业务规则校验
        validate(request);

        // 构建实体并注入租户标识（当前单租户，默认 "default"）
        TodoDO todo = new TodoDO();
        todo.setName(request.getName().trim());
        todo.setDescription(request.getDescription());
        todo.setTenantId(TodoConstants.DEFAULT_TENANT_ID);

        // 持久化
        TodoDO saved;
        try {
            saved = todoRepository.save(todo);
        } catch (DataAccessException ex) {
            logger.error("待办事项持久化失败, name: {}, errorMessage: {}", request.getName(), ex.getMessage(), ex);
            throw new BusinessException(TodoErrorCodeEnum.SYSTEM_ERROR.getErrorCode(),
                    TodoErrorCodeEnum.SYSTEM_ERROR.getErrorMessage());
        }

        logger.info("待办事项创建成功, id: {}, tenantId: {}", saved.getId(), saved.getTenantId());
        return new TodoCreateResult(saved.getId());
    }

    /**
     * 业务规则校验
     * R01: name 去除首尾空白后非空
     * R02: name 长度 ≤ 100 字符
     * R03: description 长度 ≤ 500 字符
     *
     * @param request 创建请求
     */
    private void validate(TodoCreateRequest request) {
        String name = request.getName();
        if (name == null || StringUtils.isBlank(name.trim())) {
            throw new BusinessException(TodoErrorCodeEnum.NAME_BLANK.getErrorCode(),
                    TodoErrorCodeEnum.NAME_BLANK.getErrorMessage());
        }
        if (name.trim().length() > TodoConstants.MAX_NAME_LENGTH) {
            throw new BusinessException(TodoErrorCodeEnum.NAME_TOO_LONG.getErrorCode(),
                    TodoErrorCodeEnum.NAME_TOO_LONG.getErrorMessage());
        }
        String description = request.getDescription();
        if (description != null && description.length() > TodoConstants.MAX_DESCRIPTION_LENGTH) {
            throw new BusinessException(TodoErrorCodeEnum.DESCRIPTION_TOO_LONG.getErrorCode(),
                    TodoErrorCodeEnum.DESCRIPTION_TOO_LONG.getErrorMessage());
        }
    }
}

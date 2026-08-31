package com.antdigital.todo.service.impl;

import com.antdigital.todo.common.constant.TodoConstants;
import com.antdigital.todo.common.exception.BusinessException;
import com.antdigital.todo.dao.mapper.TodoItemMapper;
import com.antdigital.todo.model.dto.TodoItemCreateRequest;
import com.antdigital.todo.model.entity.TodoItemDO;
import com.antdigital.todo.service.TodoItemService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 待办事项业务服务实现类
 *
 * @author AiWork
 * @date 2026/08/31
 */
@Service
public class TodoItemServiceImpl implements TodoItemService {

    private static final Logger logger = LoggerFactory.getLogger(TodoItemServiceImpl.class);

    private final TodoItemMapper todoItemMapper;

    /**
     * 创建人标识，由网关透传或请求上下文获取；
     * 假设：内部用户场景，通过配置或请求头透传，本期简化为配置注入。
     */
    @Value("${todo.creator:internal-user}")
    private String creator;

    /**
     * 构造注入 Mapper
     *
     * @param todoItemMapper 待办事项 Mapper
     */
    public TodoItemServiceImpl(TodoItemMapper todoItemMapper) {
        this.todoItemMapper = todoItemMapper;
    }

    /**
     * 创建待办事项，持久化保存并返回事项ID
     *
     * @param request 创建请求
     * @return 事项ID
     */
    @Override
    public Long createTodoItem(TodoItemCreateRequest request) {
        // 二次校验（@Valid 已做第一层，Service 层做防御性校验）
        validateCreateRequest(request);

        // 校验创建人标识
        if (StringUtils.isBlank(creator)) {
            logger.warn("无法获取操作人信息");
            throw new BusinessException(
                    TodoConstants.CODE_SYSTEM_ERROR,
                    TodoConstants.MSG_CREATOR_MISSING);
        }

        // 组装数据对象，显式指定 Asia/Shanghai 时区与设计 GMT+8 一致
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        TodoItemDO todoItem = new TodoItemDO();
        todoItem.setTitle(request.getTitle());
        todoItem.setDescription(request.getDescription());
        todoItem.setCreator(creator);
        todoItem.setGmtCreate(now);
        todoItem.setGmtModified(now);

        // 持久化
        int rows;
        try {
            rows = todoItemMapper.insert(todoItem);
        } catch (Exception e) {
            logger.error("待办事项写入失败, title: {}", request.getTitle(), e);
            throw new BusinessException(
                    TodoConstants.CODE_SYSTEM_ERROR,
                    TodoConstants.MSG_SYSTEM_ERROR,
                    e);
        }

        if (rows <= 0 || todoItem.getId() == null) {
            logger.error("待办事项写入未返回主键, rows: {}", rows);
            throw new BusinessException(
                    TodoConstants.CODE_SYSTEM_ERROR,
                    TodoConstants.MSG_SYSTEM_ERROR);
        }

        logger.info("待办事项创建成功, id: {}, creator: {}", todoItem.getId(), creator);
        return todoItem.getId();
    }

    /**
     * 防御性参数校验（Service 层兜底）
     *
     * @param request 创建请求
     */
    private void validateCreateRequest(TodoItemCreateRequest request) {
        if (request == null) {
            throw new BusinessException(
                    TodoConstants.CODE_TITLE_EMPTY,
                    TodoConstants.MSG_TITLE_EMPTY);
        }

        String title = request.getTitle();
        if (StringUtils.isBlank(title)) {
            throw new BusinessException(
                    TodoConstants.CODE_TITLE_EMPTY,
                    TodoConstants.MSG_TITLE_EMPTY);
        }

        if (title.length() > TodoConstants.TITLE_MAX_LENGTH) {
            throw new BusinessException(
                    TodoConstants.CODE_TITLE_TOO_LONG,
                    TodoConstants.MSG_TITLE_TOO_LONG);
        }

        String description = request.getDescription();
        if (description != null && description.length() > TodoConstants.DESCRIPTION_MAX_LENGTH) {
            throw new BusinessException(
                    TodoConstants.CODE_DESCRIPTION_TOO_LONG,
                    TodoConstants.MSG_DESCRIPTION_TOO_LONG);
        }
    }
}

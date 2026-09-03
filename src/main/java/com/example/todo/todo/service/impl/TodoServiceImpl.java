package com.example.todo.todo.service.impl;

import com.example.todo.common.exception.BusinessException;
import com.example.todo.todo.dao.entity.TodoItemDO;
import com.example.todo.todo.dao.mapper.TodoItemMapper;
import com.example.todo.todo.model.dto.CreateTodoRequest;
import com.example.todo.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 待办事项 - 业务服务实现
 */
@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoServiceImpl.class);

    private final TodoItemMapper todoItemMapper;

    public TodoServiceImpl(TodoItemMapper todoItemMapper) {
        this.todoItemMapper = todoItemMapper;
    }

    @Override
    public Long createTodo(CreateTodoRequest request) {
        // 参数校验（防御性校验）
        String title = request.getTitle();
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessException("TODO_001", "事项名称不能为空");
        }
        if (title.length() > 100) {
            throw new BusinessException("TODO_002", "事项名称长度超过限制（100字符）");
        }
        String description = request.getDescription();
        if (description != null && description.length() > 500) {
            throw new BusinessException("TODO_003", "事项描述长度超过限制（500字符）");
        }

        // 构建实体
        // 当前版本暂未接入统一登录态，使用固定用户ID
        // 后续接入统一登录态后，从 SecurityContext 中获取
        String userId = "SYSTEM";
        TodoItemDO todoItem = new TodoItemDO();
        todoItem.setUserId(userId);
        todoItem.setTitle(title.trim());
        todoItem.setDescription(description != null ? description.trim() : null);
        todoItem.setStatus(0); // 0-待办
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        todoItem.setGmtCreate(now);
        todoItem.setGmtModified(now);

        // 写入数据库
        int result = todoItemMapper.insert(todoItem);
        if (result != 1) {
            log.error("创建待办事项失败，数据库写入未生效: title={}, userId={}", title, userId);
            throw new BusinessException("B0001", "系统繁忙，请稍后重试");
        }

        log.info("创建待办事项成功: id={}, title={}, userId={}", todoItem.getId(), title, userId);
        return todoItem.getId();
    }
}
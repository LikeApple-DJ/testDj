package com.org.module.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.org.module.context.UserContext;
import com.org.module.dto.TodoDTO;
import com.org.module.entity.Todo;
import com.org.module.exception.BusinessException;
import com.org.module.mapper.TodoMapper;
import com.org.module.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 待办事项服务实现
 */
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo> implements TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoServiceImpl.class);

    /** 错误码：未获取到登录用户信息 */
    private static final String CODE_NO_LOGIN_USER = "TODO_003";

    private final UserContext userContext;

    public TodoServiceImpl(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTodo(TodoDTO dto) {
        // R03：创建人 ID 由登录上下文注入，前端不传
        Long creatorId = userContext.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(CODE_NO_LOGIN_USER,
                        "未获取到登录用户信息，无法确定创建人"));

        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setCreatorId(creatorId);
        save(todo);

        // 日志仅打印 title 与 creator_id，避免完整打印长描述
        log.info("待办创建成功: creator_id={}, todo_id={}, title={}",
                creatorId, todo.getId(), dto.getTitle());
    }
}

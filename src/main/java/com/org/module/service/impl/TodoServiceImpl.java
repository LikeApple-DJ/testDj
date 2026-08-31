package com.org.module.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.org.module.dto.TodoCreateRequest;
import com.org.module.entity.TodoItem;
import com.org.module.mapper.TodoMapper;
import com.org.module.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 待办事项业务服务实现。
 */
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, TodoItem> implements TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTodo(TodoCreateRequest request, String creator) {
        LocalDateTime now = LocalDateTime.now();
        TodoItem item = new TodoItem();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setCreator(creator);
        item.setIsDeleted(0);
        item.setGmtCreate(now);
        item.setGmtModified(now);
        save(item);
        log.info("待办事项创建成功, id={}, creator={}", item.getId(), creator);
        return item.getId();
    }
}

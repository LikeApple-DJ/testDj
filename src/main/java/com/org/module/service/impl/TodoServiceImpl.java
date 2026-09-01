package com.org.module.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.org.module.dto.TodoDTO;
import com.org.module.dto.TodoVO;
import com.org.module.entity.Todo;
import com.org.module.exception.BusinessException;
import com.org.module.mapper.TodoMapper;
import com.org.module.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 待办事项业务服务实现
 */
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo>
        implements TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoServiceImpl.class);

    private static final int TODO_STATUS_PENDING = 0;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoVO createTodo(TodoDTO dto) {
        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setStatus(TODO_STATUS_PENDING);
        boolean saved = save(todo);
        if (!saved || todo.getId() == null) {
            log.error("待办事项创建失败, title: {}", dto.getTitle());
            throw new BusinessException("B0001", "待办事项创建失败");
        }
        log.info("待办事项创建成功, id: {}", todo.getId());
        return toVO(todo);
    }

    private TodoVO toVO(Todo todo) {
        TodoVO vo = new TodoVO();
        vo.setId(todo.getId());
        vo.setTitle(todo.getTitle());
        vo.setDescription(todo.getDescription());
        vo.setStatus(todo.getStatus());
        vo.setCreatedAt(todo.getCreatedAt());
        return vo;
    }
}

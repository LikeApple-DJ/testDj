package com.org.module.controller;

import com.org.module.Result;
import com.org.module.dto.TodoCreateRequest;
import com.org.module.dto.TodoVO;
import com.org.module.service.TodoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项控制器。
 */
@RestController
@RequestMapping("/api/todo")
public class TodoController {

    private static final Logger log = LoggerFactory.getLogger(TodoController.class);

    private static final String USER_ID_HEADER = "X-User-Id";

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 新增待办事项。
     *
     * @param creator 创建人标识，来源请求头 X-User-Id，缺失时落 null
     * @param request 新增请求
     * @return 新建待办事项主键
     */
    @PostMapping
    public Result<TodoVO> create(
            @RequestHeader(value = USER_ID_HEADER, required = false) String creator,
            @RequestBody @Valid TodoCreateRequest request) {
        Long id = todoService.createTodo(request, creator);
        return Result.ok(new TodoVO(id));
    }
}

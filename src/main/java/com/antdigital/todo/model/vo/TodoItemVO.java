package com.antdigital.todo.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增待办事项响应 VO
 *
 * @author AiWork
 * @date 2026/08/31
 */
@Data
public class TodoItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 创建成功的待办事项ID */
    private Long id;

    /**
     * 构造响应 VO
     *
     * @param id 待办事项ID
     */
    public TodoItemVO(Long id) {
        this.id = id;
    }

    /** Jackson 序列化需要无参构造 */
    public TodoItemVO() {
    }
}

package com.antdigital.todo.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 待办事项数据对象
 * <p>
 * 对应数据库表 todo_item，记录用户录入的事项名称与描述。
 * </p>
 *
 * @author AiWork
 * @date 2026/08/31
 */
@Data
public class TodoItemDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 系统自增主键 */
    private Long id;

    /** 待办事项名称 */
    private String title;

    /** 待办事项描述 */
    private String description;

    /** 创建人标识（用户ID/工号） */
    private String creator;

    /** 创建时间 */
    private LocalDateTime gmtCreate;

    /** 修改时间 */
    private LocalDateTime gmtModified;
}

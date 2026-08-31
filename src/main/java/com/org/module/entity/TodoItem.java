package com.org.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办事项实体，对应数据库表 todo_item。
 */
@Data
@TableName("todo_item")
public class TodoItem {

    /** 系统自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 事项名称，必填，长度 1-128 */
    private String name;

    /** 事项描述，可选，长度 <=1024 */
    private String description;

    /** 创建人标识，来源请求头 X-User-Id */
    private String creator;

    /** 逻辑删除标记：0-未删除，1-已删除 */
    @TableLogic
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime gmtCreate;

    /** 修改时间，代码逻辑维护 */
    private LocalDateTime gmtModified;
}

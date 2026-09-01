package com.org.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办事项实体
 */
@Data
@TableName("todo")
public class Todo {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 事项名称 */
    private String title;

    /** 事项描述（选填） */
    private String description;

    /** 创建人ID（来自登录上下文） */
    private Long creatorId;

    /** 乐观锁版本号 */
    @Version
    private Long version;

    /** 逻辑删除 0=否 1=是 */
    @TableLogic
    private Integer isDeleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 修改时间 */
    private LocalDateTime updatedAt;
}

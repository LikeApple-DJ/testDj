package com.org.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 待办事项数据对象
 */
@Data
@ToString
@TableName("todo")
public class Todo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Integer status;
    @Version
    private Long version;
    @TableLogic
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

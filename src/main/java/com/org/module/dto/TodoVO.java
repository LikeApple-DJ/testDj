package com.org.module.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办事项展示 VO
 */
@Data
public class TodoVO {
    private Long id;
    private String title;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
}

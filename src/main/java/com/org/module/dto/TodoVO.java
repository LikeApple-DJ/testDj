package com.org.module.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增待办事项响应体。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoVO {

    /** 新建待办事项主键 */
    private Long id;
}

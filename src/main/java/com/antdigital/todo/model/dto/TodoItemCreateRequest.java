package com.antdigital.todo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增待办事项请求 DTO
 *
 * @author AiWork
 * @date 2026/08/31
 */
@Data
public class TodoItemCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 待办事项名称，1~100字符 */
    @NotBlank(message = "事项名称不能为空")
    @Size(max = 100, message = "事项名称长度需在1~100字符")
    private String title;

    /** 待办事项描述，最长500字符 */
    @Size(max = 500, message = "事项描述长度不超过500字符")
    private String description;
}

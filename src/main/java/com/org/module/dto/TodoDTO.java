package com.org.module.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增待办事项请求 DTO
 */
@Data
public class TodoDTO {
    @NotBlank(message = "事项名称不能为空")
    @Size(max = 200, message = "事项名称长度不能超过200")
    private String title;

    @Size(max = 1000, message = "事项描述长度不能超过1000")
    private String description;
}

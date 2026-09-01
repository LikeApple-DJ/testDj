package com.org.module.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 待办事项创建请求
 */
@Data
public class TodoDTO {

    /** 事项名称，长度 1-200 */
    @NotBlank(message = "事项名称不能为空")
    @Size(max = 200, message = "事项名称长度超出 200")
    private String title;

    /** 事项描述，选填，长度 0-1000 */
    @Size(max = 1000, message = "事项描述长度超出 1000")
    private String description;
}

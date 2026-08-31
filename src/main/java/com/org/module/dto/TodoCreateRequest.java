package com.org.module.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增待办事项请求体。
 */
@Data
public class TodoCreateRequest {

    /** 事项名称，必填，长度 1-128 */
    @NotBlank(message = "事项名称不能为空或长度超限")
    @Size(min = 1, max = 128, message = "事项名称不能为空或长度超限")
    private String name;

    /** 事项描述，可选，长度 <=1024 */
    @Size(max = 1024, message = "事项描述长度超限")
    private String description;
}

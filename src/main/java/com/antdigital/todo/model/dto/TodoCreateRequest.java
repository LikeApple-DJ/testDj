package com.antdigital.todo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * 新增待办事项请求 DTO。
 *
 * <p>对应 design.md §5.1.2 W01 入参：
 * name（必填，1-128）、description（选填，0-1024）。</p>
 * <p>R01/R02 由 JSR-303 注解在 Controller 层前置校验，Service 层做兜底校验。</p>
 */
public class TodoCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 事项名称，长度1-128 */
    @NotBlank(message = "事项名称不能为空")
    @Size(min = 1, max = 128, message = "事项名称长度必须在1-128字符之间")
    private String name;

    /** 事项描述，长度0-1024 */
    @Size(max = 1024, message = "事项描述长度不能超过1024字符")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TodoCreateRequest{" +
                "name='" + name + '\'' +
                ", description='" + (description == null ? "null" : "[masked]") + '\'' +
                '}';
    }
}

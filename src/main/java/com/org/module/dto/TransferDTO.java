package com.org.module.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferDTO {
    @NotNull(message = "目标部门不能为空")
    private Long newDeptId;
    @NotBlank(message = "新职位不能为空")
    private String newPosition;
    private String reason;
}

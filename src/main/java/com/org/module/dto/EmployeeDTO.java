package com.org.module.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "工号不能为空")
    private String employeeNo;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotNull(message = "所属部门不能为空")
    private Long deptId;
    private String position;
}

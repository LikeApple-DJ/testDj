package com.org.module.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ResignDTO {
    @NotNull(message = "离职日期不能为空")
    private LocalDate resignDate;
}

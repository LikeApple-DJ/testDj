package com.orgarch.employee;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ResignRequest {
    @NotNull private LocalDate resignDate;

    public LocalDate getResignDate() { return resignDate; }
    public void setResignDate(LocalDate resignDate) { this.resignDate = resignDate; }
}

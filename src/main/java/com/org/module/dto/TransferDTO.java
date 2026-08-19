package com.org.module.dto;

import lombok.Data;

@Data
public class TransferDTO {
    private Long newDeptId;
    private String newPosition;
    private String reason;
}

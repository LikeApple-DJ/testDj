package com.example.cost.dto;

import lombok.Data;

@Data
public class LaborCostQueryDTO {
    private String department;
    private String project;
    private String businessLine;
    private Long personnelId;
    private String periodType;   // month / quarter / year
    private String periodValue;
    private String role;         // dev / test / product / ops
}
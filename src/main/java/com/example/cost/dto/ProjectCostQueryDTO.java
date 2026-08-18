package com.example.cost.dto;

import lombok.Data;

@Data
public class ProjectCostQueryDTO {
    private String department;
    private String project;
    private String businessLine;
    private String periodType;
    private String periodValue;
}
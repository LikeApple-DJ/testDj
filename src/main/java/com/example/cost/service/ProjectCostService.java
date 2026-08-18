package com.example.cost.service;

import com.example.cost.dto.ProjectCostQueryDTO;
import com.example.cost.dto.ProjectCostVO;

public interface ProjectCostService {
    ProjectCostVO queryProjectStats(ProjectCostQueryDTO query);
}
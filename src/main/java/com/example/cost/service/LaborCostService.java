package com.example.cost.service;

import com.example.cost.dto.LaborCostQueryDTO;
import com.example.cost.dto.LaborCostVO;

public interface LaborCostService {
    LaborCostVO queryLaborStats(LaborCostQueryDTO query);
}
package com.example.cost.service;

import com.example.cost.dto.DashboardVO;

public interface DashboardService {
    DashboardVO getDashboard(String periodType, String periodValue);
}
package com.example.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.cost.dto.DashboardVO;
import com.example.cost.entity.LaborCost;
import com.example.cost.entity.ProjectCost;
import com.example.cost.mapper.LaborCostMapper;
import com.example.cost.mapper.ProjectCostMapper;
import com.example.cost.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final LaborCostMapper laborCostMapper;
    private final ProjectCostMapper projectCostMapper;

    @Override
    public DashboardVO getDashboard(String periodType, String periodValue) {
        LambdaQueryWrapper<LaborCost> laborWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<ProjectCost> projectWrapper = new LambdaQueryWrapper<>();
        applyPeriod(laborWrapper, projectWrapper, periodType, periodValue);

        List<LaborCost> laborCosts = laborCostMapper.selectList(laborWrapper);
        List<ProjectCost> projectCosts = projectCostMapper.selectList(projectWrapper);

        DashboardVO vo = new DashboardVO();

        // 人力成本块
        BigDecimal totalLabor = laborCosts.stream()
                .map(LaborCost::getCostAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, BigDecimal> byRole = laborCosts.stream()
                .collect(Collectors.groupingBy(LaborCost::getRole,
                        Collectors.reducing(BigDecimal.ZERO, LaborCost::getCostAmount, BigDecimal::add)));
        DashboardVO.LaborCostBlock laborBlock = new DashboardVO.LaborCostBlock();
        laborBlock.setTotal(totalLabor);
        laborBlock.setByRole(byRole.entrySet().stream().map(e -> {
            DashboardVO.RoleCost rc = new DashboardVO.RoleCost();
            rc.setRole(e.getKey());
            rc.setCost(e.getValue());
            return rc;
        }).collect(Collectors.toList()));
        vo.setLaborCost(laborBlock);

        // 项目成本块
        BigDecimal totalBudget = projectCosts.stream()
                .map(ProjectCost::getBudgetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalActual = projectCosts.stream()
                .map(ProjectCost::getActualAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        DashboardVO.ProjectCostBlock projectBlock = new DashboardVO.ProjectCostBlock();
        projectBlock.setTotalBudget(totalBudget);
        projectBlock.setTotalActual(totalActual);
        projectBlock.setTotalRatio(totalBudget.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO : totalActual.divide(totalBudget, 4, RoundingMode.HALF_UP));
        vo.setProjectCost(projectBlock);

        // 趋势：根据 periodType 聚合
        List<DashboardVO.TrendItem> trend = new ArrayList<>();
        if ("year".equals(periodType)) {
            Map<String, BigDecimal> laborByMonth = laborCosts.stream()
                    .collect(Collectors.groupingBy(LaborCost::getCostMonth,
                            Collectors.reducing(BigDecimal.ZERO, LaborCost::getCostAmount, BigDecimal::add)));
            Map<String, BigDecimal> projectByMonth = projectCosts.stream()
                    .collect(Collectors.groupingBy(ProjectCost::getCostMonth,
                            Collectors.reducing(BigDecimal.ZERO, ProjectCost::getActualAmount, BigDecimal::add)));
            Set<String> months = new TreeSet<>();
            months.addAll(laborByMonth.keySet());
            months.addAll(projectByMonth.keySet());
            for (String m : months) {
                DashboardVO.TrendItem item = new DashboardVO.TrendItem();
                item.setLabel(m);
                item.setLaborCost(laborByMonth.getOrDefault(m, BigDecimal.ZERO));
                item.setProjectCost(projectByMonth.getOrDefault(m, BigDecimal.ZERO));
                trend.add(item);
            }
        }
        vo.setTrend(trend);

        // 超支 Top 项目
        List<DashboardVO.TopOverspend> top = projectCosts.stream()
                .collect(Collectors.groupingBy(ProjectCost::getProjectId,
                        Collectors.reducing(BigDecimal.ZERO,
                                pc -> pc.getActualAmount().subtract(pc.getBudgetAmount()),
                                BigDecimal::add)))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    DashboardVO.TopOverspend t = new DashboardVO.TopOverspend();
                    t.setProjectName("Project-" + e.getKey());
                    t.setOverspend(e.getValue());
                    return t;
                }).collect(Collectors.toList());
        vo.setTopOverspendProjects(top);

        return vo;
    }

    private void applyPeriod(LambdaQueryWrapper<LaborCost> lw, LambdaQueryWrapper<ProjectCost> pw,
                             String periodType, String periodValue) {
        switch (periodType) {
            case "month" -> {
                lw.eq(LaborCost::getCostMonth, periodValue);
                pw.eq(ProjectCost::getCostMonth, periodValue);
            }
            case "quarter" -> {
                lw.eq(LaborCost::getCostQuarter, periodValue);
                pw.eq(ProjectCost::getCostQuarter, periodValue);
            }
            case "year" -> {
                lw.eq(LaborCost::getCostYear, periodValue);
                pw.eq(ProjectCost::getCostYear, periodValue);
            }
        }
    }
}
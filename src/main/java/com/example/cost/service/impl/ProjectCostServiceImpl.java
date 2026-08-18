package com.example.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.cost.dto.ProjectCostQueryDTO;
import com.example.cost.dto.ProjectCostVO;
import com.example.cost.entity.ProjectCost;
import com.example.cost.mapper.ProjectCostMapper;
import com.example.cost.service.ProjectCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectCostServiceImpl implements ProjectCostService {

    private final ProjectCostMapper projectCostMapper;

    @Override
    public ProjectCostVO queryProjectStats(ProjectCostQueryDTO query) {
        LambdaQueryWrapper<ProjectCost> wrapper = new LambdaQueryWrapper<>();
        applyPeriodFilter(wrapper, query.getPeriodType(), query.getPeriodValue());
        List<ProjectCost> records = projectCostMapper.selectList(wrapper);

        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        List<ProjectCostVO.Item> items = new ArrayList<>();

        for (ProjectCost pc : records) {
            totalBudget = totalBudget.add(pc.getBudgetAmount());
            totalActual = totalActual.add(pc.getActualAmount());

            ProjectCostVO.Item item = new ProjectCostVO.Item();
            item.setProjectName("Project-" + pc.getProjectId());
            item.setBudget(pc.getBudgetAmount());
            item.setActual(pc.getActualAmount());
            item.setRatio(pc.getBudgetAmount().compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : pc.getActualAmount().divide(pc.getBudgetAmount(), 4, RoundingMode.HALF_UP));
            item.setOverspend(pc.getActualAmount().subtract(pc.getBudgetAmount()));
            items.add(item);
        }

        ProjectCostVO.Summary summary = new ProjectCostVO.Summary();
        summary.setTotalBudget(totalBudget);
        summary.setTotalActual(totalActual);
        summary.setTotalRatio(totalBudget.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO : totalActual.divide(totalBudget, 4, RoundingMode.HALF_UP));
        summary.setTotalOverspend(totalActual.subtract(totalBudget));

        ProjectCostVO vo = new ProjectCostVO();
        vo.setSummary(summary);
        vo.setItems(items);
        return vo;
    }

    private void applyPeriodFilter(LambdaQueryWrapper<ProjectCost> wrapper, String periodType, String periodValue) {
        if (!StringUtils.hasText(periodType) || !StringUtils.hasText(periodValue)) return;
        switch (periodType) {
            case "month" -> wrapper.eq(ProjectCost::getCostMonth, periodValue);
            case "quarter" -> wrapper.eq(ProjectCost::getCostQuarter, periodValue);
            case "year" -> wrapper.eq(ProjectCost::getCostYear, periodValue);
        }
    }
}
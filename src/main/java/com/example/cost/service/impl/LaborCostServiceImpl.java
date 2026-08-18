package com.example.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.cost.dto.LaborCostQueryDTO;
import com.example.cost.dto.LaborCostVO;
import com.example.cost.entity.LaborCost;
import com.example.cost.mapper.LaborCostMapper;
import com.example.cost.service.LaborCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaborCostServiceImpl implements LaborCostService {

    private final LaborCostMapper laborCostMapper;

    @Override
    public LaborCostVO queryLaborStats(LaborCostQueryDTO query) {
        LambdaQueryWrapper<LaborCost> wrapper = new LambdaQueryWrapper<>();
        applyPeriodFilter(wrapper, query.getPeriodType(), query.getPeriodValue());
        if (StringUtils.hasText(query.getRole())) {
            wrapper.eq(LaborCost::getRole, query.getRole());
        }
        List<LaborCost> records = laborCostMapper.selectList(wrapper);

        // 按角色分组
        Map<String, List<LaborCost>> byRole = records.stream()
                .collect(Collectors.groupingBy(LaborCost::getRole));
        BigDecimal totalCost = records.stream()
                .map(LaborCost::getCostAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Set<Long> distinctPersonnel = records.stream()
                .map(LaborCost::getPersonnelId).collect(Collectors.toSet());
        int headcount = distinctPersonnel.size();

        List<LaborCostVO.Breakdown> breakdowns = new ArrayList<>();
        for (Map.Entry<String, List<LaborCost>> entry : byRole.entrySet()) {
            BigDecimal roleCost = entry.getValue().stream()
                    .map(LaborCost::getCostAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            int roleHeadcount = (int) entry.getValue().stream()
                    .map(LaborCost::getPersonnelId).distinct().count();
            LaborCostVO.Breakdown bd = new LaborCostVO.Breakdown();
            bd.setRole(entry.getKey());
            bd.setCost(roleCost);
            bd.setHeadcount(roleHeadcount);
            bd.setRatio(totalCost.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : roleCost.divide(totalCost, 4, RoundingMode.HALF_UP));
            breakdowns.add(bd);
        }

        LaborCostVO.Summary summary = new LaborCostVO.Summary();
        summary.setTotalLaborCost(totalCost);
        summary.setAvgCostPerPerson(headcount == 0 ? BigDecimal.ZERO
                : totalCost.divide(BigDecimal.valueOf(headcount), 2, RoundingMode.HALF_UP));
        summary.setHeadcount(headcount);

        LaborCostVO vo = new LaborCostVO();
        vo.setSummary(summary);
        vo.setBreakdown(breakdowns);
        return vo;
    }

    private void applyPeriodFilter(LambdaQueryWrapper<LaborCost> wrapper, String periodType, String periodValue) {
        if (!StringUtils.hasText(periodType) || !StringUtils.hasText(periodValue)) return;
        switch (periodType) {
            case "month" -> wrapper.eq(LaborCost::getCostMonth, periodValue);
            case "quarter" -> wrapper.eq(LaborCost::getCostQuarter, periodValue);
            case "year" -> wrapper.eq(LaborCost::getCostYear, periodValue);
        }
    }
}
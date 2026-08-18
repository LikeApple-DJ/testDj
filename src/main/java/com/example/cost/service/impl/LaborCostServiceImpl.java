package com.example.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.cost.dto.LaborCostQueryDTO;
import com.example.cost.dto.LaborCostVO;
import com.example.cost.entity.BusinessLine;
import com.example.cost.entity.Department;
import com.example.cost.entity.LaborCost;
import com.example.cost.entity.Project;
import com.example.cost.mapper.BusinessLineMapper;
import com.example.cost.mapper.DepartmentMapper;
import com.example.cost.mapper.LaborCostMapper;
import com.example.cost.mapper.ProjectMapper;
import com.example.cost.service.LaborCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaborCostServiceImpl implements LaborCostService {

    private final LaborCostMapper laborCostMapper;
    private final DepartmentMapper departmentMapper;
    private final ProjectMapper projectMapper;
    private final BusinessLineMapper businessLineMapper;

    @Override
    public LaborCostVO queryLaborStats(LaborCostQueryDTO query) {
        LambdaQueryWrapper<LaborCost> wrapper = new LambdaQueryWrapper<>();
        applyPeriodFilter(wrapper, query.getPeriodType(), query.getPeriodValue());

        // 维度筛选：解析名称 → ID，过滤查询
        applyDimensionFilter(wrapper, query);

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

    /**
     * 应用维度筛选条件：将名称解析为 ID 后过滤。
     */
    private void applyDimensionFilter(LambdaQueryWrapper<LaborCost> wrapper, LaborCostQueryDTO query) {
        if (StringUtils.hasText(query.getDepartment())) {
            Department dept = departmentMapper.selectOne(
                    new LambdaQueryWrapper<Department>().eq(Department::getName, query.getDepartment()));
            if (dept != null) {
                wrapper.eq(LaborCost::getDepartmentId, dept.getId());
            } else {
                wrapper.eq(LaborCost::getDepartmentId, -1L);
            }
        }
        if (StringUtils.hasText(query.getProject())) {
            Project proj = projectMapper.selectOne(
                    new LambdaQueryWrapper<Project>().eq(Project::getName, query.getProject()));
            if (proj != null) {
                wrapper.eq(LaborCost::getProjectId, proj.getId());
            } else {
                wrapper.eq(LaborCost::getProjectId, -1L);
            }
        }
        if (StringUtils.hasText(query.getBusinessLine())) {
            BusinessLine bl = businessLineMapper.selectOne(
                    new LambdaQueryWrapper<BusinessLine>().eq(BusinessLine::getName, query.getBusinessLine()));
            if (bl != null) {
                wrapper.eq(LaborCost::getBusinessLineId, bl.getId());
            } else {
                wrapper.eq(LaborCost::getBusinessLineId, -1L);
            }
        }
        if (query.getPersonnelId() != null) {
            wrapper.eq(LaborCost::getPersonnelId, query.getPersonnelId());
        }
    }

    private void applyPeriodFilter(LambdaQueryWrapper<LaborCost> wrapper, String periodType, String periodValue) {
        if (!StringUtils.hasText(periodType) || !StringUtils.hasText(periodValue)) return;
        switch (periodType) {
            case "month" -> wrapper.eq(LaborCost::getCostMonth, periodValue);
            case "quarter" -> wrapper.eq(LaborCost::getCostQuarter, periodValue);
            case "year" -> wrapper.eq(LaborCost::getCostYear, periodValue);
            default -> throw new IllegalArgumentException("无效的 periodType: " + periodType);
        }
    }
}
package com.example.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.cost.dto.ProjectCostQueryDTO;
import com.example.cost.dto.ProjectCostVO;
import com.example.cost.entity.BusinessLine;
import com.example.cost.entity.Department;
import com.example.cost.entity.Project;
import com.example.cost.entity.ProjectCost;
import com.example.cost.mapper.BusinessLineMapper;
import com.example.cost.mapper.DepartmentMapper;
import com.example.cost.mapper.ProjectCostMapper;
import com.example.cost.mapper.ProjectMapper;
import com.example.cost.service.ProjectCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectCostServiceImpl implements ProjectCostService {

    private final ProjectCostMapper projectCostMapper;
    private final ProjectMapper projectMapper;
    private final DepartmentMapper departmentMapper;
    private final BusinessLineMapper businessLineMapper;

    @Override
    public ProjectCostVO queryProjectStats(ProjectCostQueryDTO query) {
        LambdaQueryWrapper<ProjectCost> wrapper = new LambdaQueryWrapper<>();
        applyPeriodFilter(wrapper, query.getPeriodType(), query.getPeriodValue());

        // 维度筛选：解析名称 → ID，过滤查询
        applyDimensionFilter(wrapper, query);

        List<ProjectCost> records = projectCostMapper.selectList(wrapper);

        // 预加载项目、部门、业务线名称映射
        Map<Long, Project> projectMap = loadProjectMap(records);
        Map<Long, Department> deptMap = loadDepartmentMap(records);
        Map<Long, BusinessLine> blMap = loadBusinessLineMap(records);

        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        List<ProjectCostVO.Item> items = new ArrayList<>();

        for (ProjectCost pc : records) {
            totalBudget = totalBudget.add(pc.getBudgetAmount());
            totalActual = totalActual.add(pc.getActualAmount());

            Project project = projectMap.get(pc.getProjectId());
            Department dept = deptMap.get(pc.getDepartmentId());
            BusinessLine bl = blMap.get(pc.getBusinessLineId());

            ProjectCostVO.Item item = new ProjectCostVO.Item();
            item.setProjectName(project != null ? project.getName() : "未知项目");
            item.setBudget(pc.getBudgetAmount());
            item.setActual(pc.getActualAmount());
            item.setRatio(pc.getBudgetAmount().compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : pc.getActualAmount().divide(pc.getBudgetAmount(), 4, RoundingMode.HALF_UP));
            item.setOverspend(pc.getActualAmount().subtract(pc.getBudgetAmount()));
            item.setDepartment(dept != null ? dept.getName() : "");
            item.setBusinessLine(bl != null ? bl.getName() : "");
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
            default -> throw new IllegalArgumentException("无效的 periodType: " + periodType);
        }
    }

    /**
     * 应用维度筛选条件：将名称解析为 ID 后过滤。
     */
    private void applyDimensionFilter(LambdaQueryWrapper<ProjectCost> wrapper, ProjectCostQueryDTO query) {
        if (StringUtils.hasText(query.getDepartment())) {
            Department dept = departmentMapper.selectOne(
                    new LambdaQueryWrapper<Department>().eq(Department::getName, query.getDepartment()));
            if (dept != null) {
                wrapper.eq(ProjectCost::getDepartmentId, dept.getId());
            } else {
                wrapper.eq(ProjectCost::getDepartmentId, -1L);
            }
        }
        if (StringUtils.hasText(query.getProject())) {
            Project proj = projectMapper.selectOne(
                    new LambdaQueryWrapper<Project>().eq(Project::getName, query.getProject()));
            if (proj != null) {
                wrapper.eq(ProjectCost::getProjectId, proj.getId());
            } else {
                wrapper.eq(ProjectCost::getProjectId, -1L);
            }
        }
        if (StringUtils.hasText(query.getBusinessLine())) {
            BusinessLine bl = businessLineMapper.selectOne(
                    new LambdaQueryWrapper<BusinessLine>().eq(BusinessLine::getName, query.getBusinessLine()));
            if (bl != null) {
                wrapper.eq(ProjectCost::getBusinessLineId, bl.getId());
            } else {
                wrapper.eq(ProjectCost::getBusinessLineId, -1L);
            }
        }
    }

    private Map<Long, Project> loadProjectMap(List<ProjectCost> records) {
        List<Long> projectIds = records.stream().map(ProjectCost::getProjectId).distinct().collect(Collectors.toList());
        if (projectIds.isEmpty()) return Map.of();
        return projectMapper.selectBatchIds(projectIds).stream()
                .collect(Collectors.toMap(Project::getId, Function.identity()));
    }

    private Map<Long, Department> loadDepartmentMap(List<ProjectCost> records) {
        List<Long> deptIds = records.stream().map(ProjectCost::getDepartmentId).distinct().collect(Collectors.toList());
        if (deptIds.isEmpty()) return Map.of();
        return departmentMapper.selectBatchIds(deptIds).stream()
                .collect(Collectors.toMap(Department::getId, Function.identity()));
    }

    private Map<Long, BusinessLine> loadBusinessLineMap(List<ProjectCost> records) {
        List<Long> blIds = records.stream().map(ProjectCost::getBusinessLineId).distinct().collect(Collectors.toList());
        if (blIds.isEmpty()) return Map.of();
        return businessLineMapper.selectBatchIds(blIds).stream()
                .collect(Collectors.toMap(BusinessLine::getId, Function.identity()));
    }
}
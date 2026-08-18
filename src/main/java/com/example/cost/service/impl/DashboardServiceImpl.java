package com.example.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.cost.dto.DashboardVO;
import com.example.cost.entity.LaborCost;
import com.example.cost.entity.Project;
import com.example.cost.entity.ProjectCost;
import com.example.cost.mapper.LaborCostMapper;
import com.example.cost.mapper.ProjectCostMapper;
import com.example.cost.mapper.ProjectMapper;
import com.example.cost.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final LaborCostMapper laborCostMapper;
    private final ProjectCostMapper projectCostMapper;
    private final ProjectMapper projectMapper;

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

        // 趋势：根据 periodType 生成时间范围标签，查询趋势数据
        List<DashboardVO.TrendItem> trend = buildTrend(periodType, periodValue);
        vo.setTrend(trend);

        // 超支 Top 项目（使用真实项目名）
        List<Long> projectIds = projectCosts.stream()
                .map(ProjectCost::getProjectId).distinct().collect(Collectors.toList());
        Map<Long, Project> projectMap = projectIds.isEmpty() ? Map.of()
                : projectMapper.selectBatchIds(projectIds).stream()
                .collect(Collectors.toMap(Project::getId, Function.identity()));

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
                    Project proj = projectMap.get(e.getKey());
                    t.setProjectName(proj != null ? proj.getName() : "未知项目");
                    t.setOverspend(e.getValue());
                    return t;
                }).collect(Collectors.toList());
        vo.setTopOverspendProjects(top);

        return vo;
    }

    /**
     * 构建趋势数据：month → 最近12个月，quarter → 最近4季度，year → 最近3年。
     */
    private List<DashboardVO.TrendItem> buildTrend(String periodType, String periodValue) {
        List<DashboardVO.TrendItem> trend = new ArrayList<>();
        switch (periodType) {
            case "month" -> {
                List<String> labels = generateLast12Months(periodValue);
                List<LaborCost> trendLabor = queryLaborByMonths(labels);
                List<ProjectCost> trendProject = queryProjectByMonths(labels);
                Map<String, BigDecimal> laborByMonth = trendLabor.stream()
                        .collect(Collectors.groupingBy(LaborCost::getCostMonth,
                                Collectors.reducing(BigDecimal.ZERO, LaborCost::getCostAmount, BigDecimal::add)));
                Map<String, BigDecimal> projectByMonth = trendProject.stream()
                        .collect(Collectors.groupingBy(ProjectCost::getCostMonth,
                                Collectors.reducing(BigDecimal.ZERO, ProjectCost::getActualAmount, BigDecimal::add)));
                for (String label : labels) {
                    DashboardVO.TrendItem item = new DashboardVO.TrendItem();
                    item.setLabel(label);
                    item.setLaborCost(laborByMonth.getOrDefault(label, BigDecimal.ZERO));
                    item.setProjectCost(projectByMonth.getOrDefault(label, BigDecimal.ZERO));
                    trend.add(item);
                }
            }
            case "quarter" -> {
                List<String> labels = generateLast4Quarters(periodValue);
                List<LaborCost> trendLabor = queryLaborByQuarters(labels);
                List<ProjectCost> trendProject = queryProjectByQuarters(labels);
                Map<String, BigDecimal> laborByQ = trendLabor.stream()
                        .collect(Collectors.groupingBy(LaborCost::getCostQuarter,
                                Collectors.reducing(BigDecimal.ZERO, LaborCost::getCostAmount, BigDecimal::add)));
                Map<String, BigDecimal> projectByQ = trendProject.stream()
                        .collect(Collectors.groupingBy(ProjectCost::getCostQuarter,
                                Collectors.reducing(BigDecimal.ZERO, ProjectCost::getActualAmount, BigDecimal::add)));
                for (String label : labels) {
                    DashboardVO.TrendItem item = new DashboardVO.TrendItem();
                    item.setLabel(label);
                    item.setLaborCost(laborByQ.getOrDefault(label, BigDecimal.ZERO));
                    item.setProjectCost(projectByQ.getOrDefault(label, BigDecimal.ZERO));
                    trend.add(item);
                }
            }
            case "year" -> {
                List<String> labels = generateLast3Years(periodValue);
                List<LaborCost> trendLabor = queryLaborByYears(labels);
                List<ProjectCost> trendProject = queryProjectByYears(labels);
                Map<String, BigDecimal> laborByY = trendLabor.stream()
                        .collect(Collectors.groupingBy(LaborCost::getCostYear,
                                Collectors.reducing(BigDecimal.ZERO, LaborCost::getCostAmount, BigDecimal::add)));
                Map<String, BigDecimal> projectByY = trendProject.stream()
                        .collect(Collectors.groupingBy(ProjectCost::getCostYear,
                                Collectors.reducing(BigDecimal.ZERO, ProjectCost::getActualAmount, BigDecimal::add)));
                for (String label : labels) {
                    DashboardVO.TrendItem item = new DashboardVO.TrendItem();
                    item.setLabel(label);
                    item.setLaborCost(laborByY.getOrDefault(label, BigDecimal.ZERO));
                    item.setProjectCost(projectByY.getOrDefault(label, BigDecimal.ZERO));
                    trend.add(item);
                }
            }
            default -> throw new IllegalArgumentException("无效的 periodType: " + periodType);
        }
        return trend;
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
            default -> throw new IllegalArgumentException("无效的 periodType: " + periodType);
        }
    }

    // ---- 趋势时间范围生成 ----

    private List<String> generateLast12Months(String currentMonth) {
        List<String> months = new ArrayList<>();
        int year = Integer.parseInt(currentMonth.substring(0, 4));
        int month = Integer.parseInt(currentMonth.substring(5, 7));
        for (int i = 11; i >= 0; i--) {
            int m = month - i;
            int y = year;
            while (m <= 0) {
                m += 12;
                y--;
            }
            months.add(String.format("%04d-%02d", y, m));
        }
        return months;
    }

    private List<String> generateLast4Quarters(String currentQuarter) {
        List<String> quarters = new ArrayList<>();
        int year = Integer.parseInt(currentQuarter.substring(0, 4));
        int q = Integer.parseInt(currentQuarter.substring(5, 6));
        for (int i = 3; i >= 0; i--) {
            int qq = q - i;
            int yy = year;
            while (qq <= 0) {
                qq += 4;
                yy--;
            }
            quarters.add(String.format("%04d-Q%d", yy, qq));
        }
        return quarters;
    }

    private List<String> generateLast3Years(String currentYear) {
        int year = Integer.parseInt(currentYear);
        List<String> years = new ArrayList<>();
        for (int i = 2; i >= 0; i--) {
            years.add(String.valueOf(year - i));
        }
        return years;
    }

    // ---- 趋势数据查询 ----

    private List<LaborCost> queryLaborByMonths(List<String> months) {
        return laborCostMapper.selectList(
                new LambdaQueryWrapper<LaborCost>().in(LaborCost::getCostMonth, months));
    }

    private List<ProjectCost> queryProjectByMonths(List<String> months) {
        return projectCostMapper.selectList(
                new LambdaQueryWrapper<ProjectCost>().in(ProjectCost::getCostMonth, months));
    }

    private List<LaborCost> queryLaborByQuarters(List<String> quarters) {
        return laborCostMapper.selectList(
                new LambdaQueryWrapper<LaborCost>().in(LaborCost::getCostQuarter, quarters));
    }

    private List<ProjectCost> queryProjectByQuarters(List<String> quarters) {
        return projectCostMapper.selectList(
                new LambdaQueryWrapper<ProjectCost>().in(ProjectCost::getCostQuarter, quarters));
    }

    private List<LaborCost> queryLaborByYears(List<String> years) {
        return laborCostMapper.selectList(
                new LambdaQueryWrapper<LaborCost>().in(LaborCost::getCostYear, years));
    }

    private List<ProjectCost> queryProjectByYears(List<String> years) {
        return projectCostMapper.selectList(
                new LambdaQueryWrapper<ProjectCost>().in(ProjectCost::getCostYear, years));
    }
}
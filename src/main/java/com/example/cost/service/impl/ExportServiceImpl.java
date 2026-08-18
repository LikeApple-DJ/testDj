package com.example.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.cost.entity.BusinessLine;
import com.example.cost.entity.Department;
import com.example.cost.entity.LaborCost;
import com.example.cost.entity.Project;
import com.example.cost.entity.ProjectCost;
import com.example.cost.mapper.BusinessLineMapper;
import com.example.cost.mapper.DepartmentMapper;
import com.example.cost.mapper.LaborCostMapper;
import com.example.cost.mapper.ProjectCostMapper;
import com.example.cost.mapper.ProjectMapper;
import com.example.cost.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final LaborCostMapper laborCostMapper;
    private final ProjectCostMapper projectCostMapper;
    private final DepartmentMapper departmentMapper;
    private final ProjectMapper projectMapper;
    private final BusinessLineMapper businessLineMapper;

    @Override
    public void export(Map<String, Object> params, HttpServletResponse response) {
        String exportType = (String) params.getOrDefault("exportType", "full");
        String periodType = (String) params.get("periodType");
        String periodValue = (String) params.get("periodValue");
        String department = (String) params.get("department");
        String project = (String) params.get("project");
        String businessLine = (String) params.get("businessLine");
        String role = (String) params.get("role");
        Object personnelIdObj = params.get("personnelId");
        Long personnelId = personnelIdObj instanceof Number ? ((Number) personnelIdObj).longValue() : null;

        try (Workbook workbook = new XSSFWorkbook()) {
            if ("labor".equals(exportType) || "full".equals(exportType)) {
                createLaborSheet(workbook, periodType, periodValue, department, project, businessLine, role, personnelId);
            }
            if ("project".equals(exportType) || "full".equals(exportType)) {
                createProjectSheet(workbook, periodType, periodValue, department, project, businessLine);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String filename = URLEncoder.encode("成本统计报表.xlsx", StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);

            try (OutputStream os = response.getOutputStream()) {
                workbook.write(os);
            }
        } catch (Exception e) {
            log.error("导出失败", e);
            throw new RuntimeException("导出失败", e);
        }
    }

    private void createLaborSheet(Workbook workbook, String periodType, String periodValue,
                                  String department, String project, String businessLine,
                                  String role, Long personnelId) {
        Sheet sheet = workbook.createSheet("人力成本");
        Row header = sheet.createRow(0);
        String[] headers = {"人员ID", "角色", "成本金额", "月份", "季度", "年度"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        LambdaQueryWrapper<LaborCost> wrapper = new LambdaQueryWrapper<>();
        applyLaborPeriod(wrapper, periodType, periodValue);
        applyLaborDimensionFilter(wrapper, department, project, businessLine, role, personnelId);
        List<LaborCost> records = laborCostMapper.selectList(wrapper);

        int rowIdx = 1;
        for (LaborCost lc : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(lc.getPersonnelId());
            row.createCell(1).setCellValue(lc.getRole());
            row.createCell(2).setCellValue(lc.getCostAmount().doubleValue());
            row.createCell(3).setCellValue(lc.getCostMonth());
            row.createCell(4).setCellValue(lc.getCostQuarter());
            row.createCell(5).setCellValue(lc.getCostYear());
        }
    }

    private void createProjectSheet(Workbook workbook, String periodType, String periodValue,
                                    String department, String project, String businessLine) {
        Sheet sheet = workbook.createSheet("项目成本");
        Row header = sheet.createRow(0);
        String[] headers = {"项目ID", "预算金额", "实际消耗", "预算占比", "预计超支", "月份", "季度", "年度"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        LambdaQueryWrapper<ProjectCost> wrapper = new LambdaQueryWrapper<>();
        applyProjectPeriod(wrapper, periodType, periodValue);
        applyProjectDimensionFilter(wrapper, department, project, businessLine);
        List<ProjectCost> records = projectCostMapper.selectList(wrapper);

        int rowIdx = 1;
        for (ProjectCost pc : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(pc.getProjectId());
            row.createCell(1).setCellValue(pc.getBudgetAmount().doubleValue());
            row.createCell(2).setCellValue(pc.getActualAmount().doubleValue());
            BigDecimal ratio = pc.getBudgetAmount().compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : pc.getActualAmount().divide(pc.getBudgetAmount(), 4, RoundingMode.HALF_UP);
            row.createCell(3).setCellValue(ratio.doubleValue());
            row.createCell(4).setCellValue(pc.getActualAmount().subtract(pc.getBudgetAmount()).doubleValue());
            row.createCell(5).setCellValue(pc.getCostMonth());
            row.createCell(6).setCellValue(pc.getCostQuarter());
            row.createCell(7).setCellValue(pc.getCostYear());
        }
    }

    private void applyLaborPeriod(LambdaQueryWrapper<LaborCost> w, String pt, String pv) {
        if (!StringUtils.hasText(pt) || !StringUtils.hasText(pv)) return;
        switch (pt) {
            case "month" -> w.eq(LaborCost::getCostMonth, pv);
            case "quarter" -> w.eq(LaborCost::getCostQuarter, pv);
            case "year" -> w.eq(LaborCost::getCostYear, pv);
            default -> throw new IllegalArgumentException("无效的 periodType: " + pt);
        }
    }

    private void applyProjectPeriod(LambdaQueryWrapper<ProjectCost> w, String pt, String pv) {
        if (!StringUtils.hasText(pt) || !StringUtils.hasText(pv)) return;
        switch (pt) {
            case "month" -> w.eq(ProjectCost::getCostMonth, pv);
            case "quarter" -> w.eq(ProjectCost::getCostQuarter, pv);
            case "year" -> w.eq(ProjectCost::getCostYear, pv);
            default -> throw new IllegalArgumentException("无效的 periodType: " + pt);
        }
    }

    private void applyLaborDimensionFilter(LambdaQueryWrapper<LaborCost> wrapper,
                                           String department, String project, String businessLine,
                                           String role, Long personnelId) {
        if (StringUtils.hasText(department)) {
            Department dept = departmentMapper.selectOne(
                    new LambdaQueryWrapper<Department>().eq(Department::getName, department));
            if (dept != null) {
                wrapper.eq(LaborCost::getDepartmentId, dept.getId());
            } else {
                wrapper.eq(LaborCost::getDepartmentId, -1L);
            }
        }
        if (StringUtils.hasText(project)) {
            Project proj = projectMapper.selectOne(
                    new LambdaQueryWrapper<Project>().eq(Project::getName, project));
            if (proj != null) {
                wrapper.eq(LaborCost::getProjectId, proj.getId());
            } else {
                wrapper.eq(LaborCost::getProjectId, -1L);
            }
        }
        if (StringUtils.hasText(businessLine)) {
            BusinessLine bl = businessLineMapper.selectOne(
                    new LambdaQueryWrapper<BusinessLine>().eq(BusinessLine::getName, businessLine));
            if (bl != null) {
                wrapper.eq(LaborCost::getBusinessLineId, bl.getId());
            } else {
                wrapper.eq(LaborCost::getBusinessLineId, -1L);
            }
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(LaborCost::getRole, role);
        }
        if (personnelId != null) {
            wrapper.eq(LaborCost::getPersonnelId, personnelId);
        }
    }

    private void applyProjectDimensionFilter(LambdaQueryWrapper<ProjectCost> wrapper,
                                             String department, String project, String businessLine) {
        if (StringUtils.hasText(department)) {
            Department dept = departmentMapper.selectOne(
                    new LambdaQueryWrapper<Department>().eq(Department::getName, department));
            if (dept != null) {
                wrapper.eq(ProjectCost::getDepartmentId, dept.getId());
            } else {
                wrapper.eq(ProjectCost::getDepartmentId, -1L);
            }
        }
        if (StringUtils.hasText(project)) {
            Project proj = projectMapper.selectOne(
                    new LambdaQueryWrapper<Project>().eq(Project::getName, project));
            if (proj != null) {
                wrapper.eq(ProjectCost::getProjectId, proj.getId());
            } else {
                wrapper.eq(ProjectCost::getProjectId, -1L);
            }
        }
        if (StringUtils.hasText(businessLine)) {
            BusinessLine bl = businessLineMapper.selectOne(
                    new LambdaQueryWrapper<BusinessLine>().eq(BusinessLine::getName, businessLine));
            if (bl != null) {
                wrapper.eq(ProjectCost::getBusinessLineId, bl.getId());
            } else {
                wrapper.eq(ProjectCost::getBusinessLineId, -1L);
            }
        }
    }
}
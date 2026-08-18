package com.example.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.cost.common.DimensionVO;
import com.example.cost.entity.BusinessLine;
import com.example.cost.entity.Department;
import com.example.cost.entity.Personnel;
import com.example.cost.entity.Project;
import com.example.cost.mapper.BusinessLineMapper;
import com.example.cost.mapper.DepartmentMapper;
import com.example.cost.mapper.PersonnelMapper;
import com.example.cost.mapper.ProjectMapper;
import com.example.cost.service.DimensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DimensionServiceImpl implements DimensionService {

    private final DepartmentMapper departmentMapper;
    private final ProjectMapper projectMapper;
    private final BusinessLineMapper businessLineMapper;
    private final PersonnelMapper personnelMapper;

    @Override
    public DimensionVO getDimensions() {
        DimensionVO vo = new DimensionVO();
        vo.setDepartments(departmentMapper.selectList(null).stream()
                .map(Department::getName).collect(Collectors.toList()));
        vo.setProjects(projectMapper.selectList(null).stream()
                .map(Project::getName).collect(Collectors.toList()));
        vo.setBusinessLines(businessLineMapper.selectList(null).stream()
                .map(BusinessLine::getName).collect(Collectors.toList()));
        vo.setPersonnel(personnelMapper.selectList(null).stream()
                .map(p -> new DimensionVO.PersonnelOption(p.getId(), p.getName()))
                .collect(Collectors.toList()));
        return vo;
    }
}
package com.org.module.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.org.module.dto.EmployeeDTO;
import com.org.module.dto.ResignDTO;
import com.org.module.dto.TransferDTO;
import com.org.module.entity.Employee;
import com.org.module.entity.TransferRecord;
import com.org.module.mapper.EmployeeMapper;
import com.org.module.mapper.TransferRecordMapper;
import com.org.module.service.EmployeeService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
        implements EmployeeService {

    private final TransferRecordMapper transferRecordMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final com.org.module.service.DepartmentService departmentService;

    public EmployeeServiceImpl(TransferRecordMapper transferRecordMapper,
                               ApplicationEventPublisher eventPublisher,
                               com.org.module.service.DepartmentService departmentService) {
        this.transferRecordMapper = transferRecordMapper;
        this.eventPublisher = eventPublisher;
        this.departmentService = departmentService;
    }

    @Override
    public boolean checkFieldExists(String field, String value) {
        if ("employeeNo".equals(field)) {
            return lambdaQuery().eq(Employee::getEmployeeNo, value).eq(Employee::getIsDeleted, 0).exists();
        } else if ("phone".equals(field)) {
            return lambdaQuery().eq(Employee::getPhone, value).eq(Employee::getIsDeleted, 0).exists();
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createEmployee(EmployeeDTO dto) {
        if (checkFieldExists("employeeNo", dto.getEmployeeNo())) {
            throw new com.org.module.exception.BusinessException("工号已存在");
        }
        if (checkFieldExists("phone", dto.getPhone())) {
            throw new com.org.module.exception.BusinessException("手机号已存在");
        }
        if (departmentService.getById(dto.getDeptId()) == null) {
            throw new com.org.module.exception.BusinessException("部门不存在");
        }
        Employee emp = new Employee();
        emp.setName(dto.getName());
        emp.setEmployeeNo(dto.getEmployeeNo());
        emp.setPhone(dto.getPhone());
        emp.setDeptId(dto.getDeptId());
        emp.setPosition(dto.getPosition());
        emp.setStatus(1);
        save(emp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferEmployee(Long id, TransferDTO dto) {
        Employee employee = getById(id);
        if (employee == null) {
            throw new com.org.module.exception.BusinessException("员工不存在");
        }
        if (departmentService.getById(dto.getNewDeptId()) == null) {
            throw new com.org.module.exception.BusinessException("目标部门不存在");
        }
        Long oldDeptId = employee.getDeptId();
        String oldPosition = employee.getPosition();

        employee.setDeptId(dto.getNewDeptId());
        employee.setPosition(dto.getNewPosition());
        boolean updated = updateById(employee);
        if (!updated) {
            throw new com.org.module.exception.BusinessException("该员工信息已被他人修改，请刷新重试");
        }

        TransferRecord record = new TransferRecord();
        record.setEmployeeId(id);
        record.setOldDeptId(oldDeptId);
        record.setNewDeptId(dto.getNewDeptId());
        record.setOldPosition(oldPosition);
        record.setNewPosition(dto.getNewPosition());
        record.setReason(dto.getReason());
        transferRecordMapper.insert(record);

        eventPublisher.publishEvent(new com.org.module.event.EmployeeTransferredEvent(id, oldDeptId, dto.getNewDeptId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resignEmployee(Long id, ResignDTO dto) {
        Employee employee = getById(id);
        if (employee == null || employee.getStatus() == 0) {
            throw new com.org.module.exception.BusinessException("员工不存在或已离职");
        }
        employee.setStatus(0);
        employee.setIsDeleted(1);
        updateById(employee);
    }
}

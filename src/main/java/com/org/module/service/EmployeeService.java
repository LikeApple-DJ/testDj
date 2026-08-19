package com.org.module.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.org.module.dto.EmployeeDTO;
import com.org.module.dto.ResignDTO;
import com.org.module.dto.TransferDTO;
import com.org.module.entity.Employee;

public interface EmployeeService extends IService<Employee> {
    boolean checkFieldExists(String field, String value);
    void createEmployee(EmployeeDTO dto);
    void transferEmployee(Long id, TransferDTO dto);
    void resignEmployee(Long id, ResignDTO dto);
}

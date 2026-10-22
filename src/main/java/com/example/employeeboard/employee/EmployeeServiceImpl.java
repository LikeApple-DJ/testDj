package com.example.employeeboard.employee;

import com.example.employeeboard.budget.BudgetDTO;
import com.example.employeeboard.budget.BudgetService;
import com.example.employeeboard.common.BizException;
import com.example.employeeboard.common.PageResult;
import com.example.employeeboard.employee.mapper.EmployeeMapper;
import com.example.employeeboard.importing.ImportTaskDO;
import com.example.employeeboard.importing.mapper.ImportTaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 员工服务实现。
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private static final String DEFAULT_TENANT = "default";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_INACTIVE = "INACTIVE";
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");

    private static final DateTimeFormatter HIRE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private final EmployeeMapper employeeMapper;
    private final BudgetService budgetService;
    private final ImportTaskMapper importTaskMapper;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper, BudgetService budgetService,
                               ImportTaskMapper importTaskMapper) {
        this.employeeMapper = employeeMapper;
        this.budgetService = budgetService;
        this.importTaskMapper = importTaskMapper;
    }

    @Override
    public EmployeeDTO create(EmployeeCreateReq req) {
        validateFormat(req.getEmail(), req.getPhone());
        if (employeeMapper.selectByEmpNo(DEFAULT_TENANT, req.getEmpNo()) != null) {
            throw new BizException("EMP_001", "工号已存在");
        }
        EmployeeDO record = new EmployeeDO();
        record.setTenantId(DEFAULT_TENANT);
        record.setEmpNo(req.getEmpNo());
        record.setEmpName(req.getEmpName());
        record.setDeptName(req.getDeptName());
        record.setPosition(nullToEmpty(req.getPosition()));
        record.setEmail(nullToEmpty(req.getEmail()));
        record.setPhone(nullToEmpty(req.getPhone()));
        record.setHireDate(parseDate(req.getHireDate()));
        record.setStatus(STATUS_ACTIVE);
        record.setSource("MANUAL");
        try {
            employeeMapper.insert(record);
        } catch (DuplicateKeyException e) {
            throw new BizException("EMP_001", "工号已存在");
        }
        LOGGER.info("employee created, empNo={}", req.getEmpNo());
        return EmployeeDTO.fromDO(record);
    }

    @Override
    public boolean update(EmployeeUpdateReq req) {
        EmployeeDO existing = employeeMapper.selectByEmpNo(DEFAULT_TENANT, req.getEmpNo());
        if (existing == null) {
            throw new BizException("EMP_003", "员工不存在");
        }
        validateFormat(req.getEmail(), req.getPhone());
        if (StringUtils.hasText(req.getStatus())
                && !STATUS_ACTIVE.equals(req.getStatus()) && !STATUS_INACTIVE.equals(req.getStatus())) {
            throw new BizException("EMP_004", "状态流转非法");
        }
        if (req.getEmpName() != null) {
            existing.setEmpName(req.getEmpName());
        }
        if (req.getDeptName() != null) {
            existing.setDeptName(req.getDeptName());
        }
        if (req.getPosition() != null) {
            existing.setPosition(req.getPosition());
        }
        if (req.getEmail() != null) {
            existing.setEmail(req.getEmail());
        }
        if (req.getPhone() != null) {
            existing.setPhone(req.getPhone());
        }
        if (req.getHireDate() != null) {
            existing.setHireDate(parseDate(req.getHireDate()));
        }
        if (req.getStatus() != null) {
            existing.setStatus(req.getStatus());
        }
        employeeMapper.updateById(existing);
        LOGGER.info("employee updated, empNo={}", req.getEmpNo());
        return true;
    }

    @Override
    public boolean deleteByEmpNo(String empNo) {
        if (employeeMapper.selectByEmpNo(DEFAULT_TENANT, empNo) == null) {
            throw new BizException("EMP_003", "员工不存在");
        }
        employeeMapper.softDelete(DEFAULT_TENANT, empNo);
        LOGGER.info("employee soft deleted, empNo={}", empNo);
        return true;
    }

    @Override
    public PageResult<EmployeeDTO> pageQuery(EmployeeQueryReq req, boolean maskPhone) {
        long total = employeeMapper.countPage(DEFAULT_TENANT, req.getKeyword(), req.getDeptName(), req.getStatus());
        List<EmployeeDO> records = employeeMapper.selectPage(DEFAULT_TENANT, req.getKeyword(),
                req.getDeptName(), req.getStatus(), (req.getPageNo() - 1) * req.getPageSize(), req.getPageSize());
        List<EmployeeDTO> list = records.stream().map(r -> {
            EmployeeDTO dto = EmployeeDTO.fromDO(r);
            if (maskPhone) {
                dto.setPhone(EmployeeDTO.maskPhone(dto.getPhone()));
            }
            return dto;
        }).collect(Collectors.toList());
        return new PageResult<>(total, req.getPageNo(), req.getPageSize(), list);
    }

    @Override
    public EmployeeDTO getByEmpNo(String empNo) {
        EmployeeDO record = employeeMapper.selectByEmpNo(DEFAULT_TENANT, empNo);
        EmployeeDTO dto = EmployeeDTO.fromDO(record);
        if (dto != null) {
            List<BudgetDTO> budgets = budgetService.queryByEmpNo(empNo);
            if (!budgets.isEmpty()) {
                dto.setLatestBudgetAmount(budgets.get(0).getAmount());
                dto.setLatestBudgetPeriod(budgets.get(0).getBudgetPeriod());
            }
        }
        return dto;
    }

    @Override
    public EmployeeSummaryDTO summary() {
        EmployeeSummaryDTO dto = new EmployeeSummaryDTO();
        dto.setTotalCount(employeeMapper.countActive(DEFAULT_TENANT));
        dto.setDeptCount(employeeMapper.countDepts(DEFAULT_TENANT));
        dto.setBudgetTotal(budgetService.sumCurrentPeriod());
        ImportTaskDO latestTask = importTaskMapper.selectLatestTask(DEFAULT_TENANT);
        if (latestTask != null) {
            dto.setRecentImport(new EmployeeSummaryDTO.RecentImport(
                    latestTask.getId(), latestTask.getSuccessCount(), latestTask.getFailCount()));
        }
        return dto;
    }

    @Override
    public boolean upsertFromImport(EmployeeDO record, boolean overwrite) {
        EmployeeDO existing = employeeMapper.selectByEmpNo(DEFAULT_TENANT, record.getEmpNo());
        if (existing == null) {
            record.setTenantId(DEFAULT_TENANT);
            record.setStatus(STATUS_ACTIVE);
            record.setSource("IMPORT");
            try {
                employeeMapper.insert(record);
            } catch (DuplicateKeyException e) {
                throw new BizException("EMP_001", "工号已存在");
            }
            return true;
        }
        if (!overwrite) {
            throw new BizException("EMP_001", "工号已存在");
        }
        record.setId(existing.getId());
        record.setStatus(existing.getStatus());
        employeeMapper.updateById(record);
        return false;
    }

    private void validateFormat(String email, String phone) {
        if (StringUtils.hasText(email) && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BizException("EMP_002", "邮箱格式非法");
        }
        if (StringUtils.hasText(phone) && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new BizException("EMP_002", "手机号格式非法");
        }
    }

    private Date parseDate(String hireDate) {
        if (!StringUtils.hasText(hireDate)) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(hireDate, HIRE_DATE_FORMATTER);
            return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            throw new BizException("EMP_002", "入职日期格式非法，应为 yyyy-MM-dd");
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

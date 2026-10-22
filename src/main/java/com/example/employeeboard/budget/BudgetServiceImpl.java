package com.example.employeeboard.budget;

import com.example.employeeboard.budget.mapper.BudgetMapper;
import com.example.employeeboard.common.BizException;
import com.example.employeeboard.employee.mapper.EmployeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 成本预算服务实现。
 */
@Service
public class BudgetServiceImpl implements BudgetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BudgetServiceImpl.class);
    private static final String DEFAULT_TENANT = "default";
    private static final String DEFAULT_CURRENCY = "CNY";
    private static final Pattern PERIOD_PATTERN =
            Pattern.compile("^\\d{4}-((0[1-9]|1[0-2])|(Q[1-4]))$");

    private final BudgetMapper budgetMapper;
    private final EmployeeMapper employeeMapper;

    public BudgetServiceImpl(BudgetMapper budgetMapper, EmployeeMapper employeeMapper) {
        this.budgetMapper = budgetMapper;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public boolean saveBudget(BudgetSaveReq req) {
        if (employeeMapper.selectByEmpNo(DEFAULT_TENANT, req.getEmpNo()) == null) {
            throw new BizException("BUD_001", "员工不存在");
        }
        if (!PERIOD_PATTERN.matcher(req.getBudgetPeriod()).matches()) {
            throw new BizException("BUD_002", "预算周期格式非法，应为 yyyy-MM 或 yyyy-Qn");
        }
        if (req.getAmount() == null || req.getAmount().signum() < 0
                || req.getAmount().scale() > 2) {
            throw new BizException("BUD_002", "金额非法，需为非负且最多两位小数");
        }
        BudgetDO record = new BudgetDO();
        record.setTenantId(DEFAULT_TENANT);
        record.setEmpNo(req.getEmpNo());
        record.setBudgetPeriod(req.getBudgetPeriod());
        record.setAmount(req.getAmount());
        record.setCurrency(StringUtils.hasText(req.getCurrency()) ? req.getCurrency() : DEFAULT_CURRENCY);
        record.setRemark(req.getRemark() == null ? "" : req.getRemark());
        budgetMapper.upsert(record);
        LOGGER.info("budget saved, empNo={}, period={}", req.getEmpNo(), req.getBudgetPeriod());
        return true;
    }

    @Override
    public List<BudgetDTO> queryByEmpNo(String empNo) {
        return budgetMapper.selectByEmpNo(DEFAULT_TENANT, empNo).stream()
                .map(BudgetDTO::fromDO).collect(Collectors.toList());
    }

    @Override
    public BigDecimal sumCurrentPeriod() {
        String period = new SimpleDateFormat("yyyy-MM").format(new Date());
        return budgetMapper.sumByPeriod(DEFAULT_TENANT, period);
    }
}

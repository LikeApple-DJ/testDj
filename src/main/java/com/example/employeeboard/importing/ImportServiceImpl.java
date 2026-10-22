package com.example.employeeboard.importing;

import com.example.employeeboard.budget.BudgetSaveReq;
import com.example.employeeboard.budget.BudgetService;
import com.example.employeeboard.common.BizException;
import com.example.employeeboard.employee.EmployeeDO;
import com.example.employeeboard.employee.EmployeeService;
import com.example.employeeboard.importing.mapper.ImportTaskMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 批量导入服务实现：逐行校验、逐行小事务落库，失败行记录明细。
 */
@Service
public class ImportServiceImpl implements ImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportServiceImpl.class);
    private static final String DEFAULT_TENANT = "default";
    private static final int MAX_ROWS = 1000;
    /** 模板表头：工号*、姓名*、部门*、岗位、邮箱、手机号、入职日期、成本预算金额、预算周期 */
    private static final String[] TEMPLATE_HEADER = {
            "工号", "姓名", "部门", "岗位", "邮箱", "手机号", "入职日期", "成本预算金额", "预算周期"};

    private final ImportTaskMapper importTaskMapper;
    private final EmployeeService employeeService;
    private final BudgetService budgetService;

    public ImportServiceImpl(ImportTaskMapper importTaskMapper, EmployeeService employeeService,
                             BudgetService budgetService) {
        this.importTaskMapper = importTaskMapper;
        this.employeeService = employeeService;
        this.budgetService = budgetService;
    }

    @Override
    public ImportTaskDTO importEmployees(MultipartFile file, String operator, boolean overwrite) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null
                || !file.getOriginalFilename().endsWith(".xlsx")) {
            throw new BizException("IMP_001", "文件为空或格式不支持，仅支持 .xlsx");
        }
        ImportTaskDO task = new ImportTaskDO();
        task.setTenantId(DEFAULT_TENANT);
        task.setOperator(operator == null ? "" : operator);
        task.setFileName(file.getOriginalFilename());
        task.setTotalCount(0);
        task.setSuccessCount(0);
        task.setFailCount(0);
        task.setStatus("PROCESSING");
        importTaskMapper.insertTask(task);

        try (InputStream in = file.getInputStream(); Workbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            validateHeader(sheet.getRow(0));
            int lastRow = sheet.getLastRowNum();
            if (lastRow > MAX_ROWS) {
                task.setStatus("FAILED");
                importTaskMapper.updateTask(task);
                throw new BizException("IMP_002", "超过 1000 行上限");
            }
            Set<String> seenEmpNos = new HashSet<>();
            int success = 0;
            int fail = 0;
            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                boolean ok = processRow(row, i + 1, task.getId(), seenEmpNos, overwrite);
                if (ok) {
                    success++;
                } else {
                    fail++;
                }
            }
            task.setTotalCount(success + fail);
            task.setSuccessCount(success);
            task.setFailCount(fail);
            task.setStatus("DONE");
            importTaskMapper.updateTask(task);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("import parse failed, taskId={}", task.getId(), e);
            task.setStatus("FAILED");
            importTaskMapper.updateTask(task);
            throw new BizException("IMP_004", "文件解析异常");
        }
        LOGGER.info("import done, taskId={}, success={}, fail={}",
                task.getId(), task.getSuccessCount(), task.getFailCount());
        return toDTO(task);
    }

    @Override
    public ImportTaskDTO getTaskDetail(Long taskId, String result) {
        ImportTaskDO task = importTaskMapper.selectTaskById(taskId);
        if (task == null) {
            throw new BizException("IMP_005", "导入批次不存在");
        }
        ImportTaskDTO dto = toDTO(task);
        List<ImportDetailDO> details = importTaskMapper.selectDetails(taskId,
                StringUtils.hasText(result) ? result : null);
        dto.setDetails(details);
        return dto;
    }

    /**
     * 逐行处理：行内校验 → 落库员工 → 可选保存预算；失败记录明细，返回 false。
     */
    private boolean processRow(Row row, int rowNo, Long taskId, Set<String> seenEmpNos, boolean overwrite) {
        String empNo = cellText(row, 0);
        String failReason = null;
        try {
            if (!StringUtils.hasText(empNo) || !StringUtils.hasText(cellText(row, 1))
                    || !StringUtils.hasText(cellText(row, 2))) {
                throw new BizException("IMP_ROW", "必填字段（工号/姓名/部门）为空");
            }
            if (!seenEmpNos.add(empNo)) {
                throw new BizException("IMP_ROW", "文件内工号重复");
            }
            EmployeeDO record = new EmployeeDO();
            record.setEmpNo(empNo);
            record.setEmpName(cellText(row, 1));
            record.setDeptName(cellText(row, 2));
            record.setPosition(emptyToDefault(cellText(row, 3)));
            record.setEmail(emptyToDefault(cellText(row, 4)));
            record.setPhone(emptyToDefault(cellText(row, 5)));
            record.setHireDate(parseHireDate(cellText(row, 6)));
            employeeService.upsertFromImport(record, overwrite);

            String amountText = cellText(row, 7);
            String period = cellText(row, 8);
            if (StringUtils.hasText(amountText)) {
                if (!StringUtils.hasText(period)) {
                    throw new BizException("IMP_ROW", "含预算金额时必须含预算周期");
                }
                BudgetSaveReq budgetReq = new BudgetSaveReq();
                budgetReq.setEmpNo(empNo);
                budgetReq.setBudgetPeriod(period);
                budgetReq.setAmount(new BigDecimal(amountText));
                budgetService.saveBudget(budgetReq);
            }
            recordDetail(taskId, rowNo, empNo, "SUCCESS", "");
            return true;
        } catch (BizException e) {
            failReason = e.getMessage();
        } catch (Exception e) {
            LOGGER.warn("import row failed, taskId={}, rowNo={}", taskId, rowNo, e);
            failReason = "行处理异常: " + e.getClass().getSimpleName();
        }
        recordDetail(taskId, rowNo, empNo, "FAIL", failReason);
        return false;
    }

    private void recordDetail(Long taskId, int rowNo, String empNo, String result, String failReason) {
        ImportDetailDO detail = new ImportDetailDO();
        detail.setTaskId(taskId);
        detail.setRowNo(rowNo);
        detail.setEmpNo(empNo == null ? "" : empNo);
        detail.setResult(result);
        detail.setFailReason(failReason == null ? "" : failReason);
        importTaskMapper.insertDetail(detail);
    }

    private void validateHeader(Row header) {
        if (header == null) {
            throw new BizException("IMP_003", "模板表头不匹配");
        }
        for (int i = 0; i < TEMPLATE_HEADER.length; i++) {
            if (!TEMPLATE_HEADER[i].equals(cellText(header, i))) {
                throw new BizException("IMP_003", "模板表头不匹配，期望第" + (i + 1) + "列为「" + TEMPLATE_HEADER[i] + "」");
            }
        }
    }

    private String cellText(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private Date parseHireDate(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            return sdf.parse(text);
        } catch (java.text.ParseException e) {
            throw new BizException("IMP_ROW", "入职日期格式非法");
        }
    }

    private String emptyToDefault(String value) {
        return value == null ? "" : value;
    }

    private ImportTaskDTO toDTO(ImportTaskDO task) {
        ImportTaskDTO dto = new ImportTaskDTO();
        dto.setTaskId(task.getId());
        dto.setFileName(task.getFileName());
        dto.setTotalCount(task.getTotalCount());
        dto.setSuccessCount(task.getSuccessCount());
        dto.setFailCount(task.getFailCount());
        dto.setStatus(task.getStatus());
        return dto;
    }
}

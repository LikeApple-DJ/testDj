package com.example.employeeboard.employee;

import com.example.employeeboard.common.PageResult;

/**
 * 员工服务。
 */
public interface EmployeeService {

    /**
     * 创建员工（S01），工号租户内唯一，默认 ACTIVE/MANUAL。
     */
    EmployeeDTO create(EmployeeCreateReq req);

    /**
     * 更新员工（S02），仅更新传入字段。
     */
    boolean update(EmployeeUpdateReq req);

    /**
     * 软删除员工（S03）。
     */
    boolean deleteByEmpNo(String empNo);

    /**
     * 分页查询（S04），maskPhone=true 时手机号脱敏。
     */
    PageResult<EmployeeDTO> pageQuery(EmployeeQueryReq req, boolean maskPhone);

    /**
     * 按工号查询（S05），不存在返回 null。
     */
    EmployeeDTO getByEmpNo(String empNo);

    /**
     * 看板汇总（S06）。
     */
    EmployeeSummaryDTO summary();

    /**
     * 导入场景：创建或覆盖更新员工，source=IMPORT。
     *
     * @return true=新建，false=覆盖更新
     */
    boolean upsertFromImport(EmployeeDO record, boolean overwrite);
}

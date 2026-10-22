package com.example.employeeboard.employee.mapper;

import com.example.employeeboard.employee.EmployeeDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 员工数据访问层。
 */
@Mapper
public interface EmployeeMapper {

    @Insert("INSERT INTO emp_employee (tenant_id, emp_no, emp_name, dept_name, position, email, phone, "
            + "hire_date, status, source) VALUES (#{tenantId}, #{empNo}, #{empName}, #{deptName}, #{position}, "
            + "#{email}, #{phone}, #{hireDate}, #{status}, #{source})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EmployeeDO record);

    @Select("SELECT * FROM emp_employee WHERE tenant_id = #{tenantId} AND emp_no = #{empNo} AND is_deleted = 0")
    EmployeeDO selectByEmpNo(@Param("tenantId") String tenantId, @Param("empNo") String empNo);

    @Update("UPDATE emp_employee SET emp_name = #{empName}, dept_name = #{deptName}, position = #{position}, "
            + "email = #{email}, phone = #{phone}, hire_date = #{hireDate}, status = #{status}, "
            + "gmt_modified = NOW() WHERE id = #{id} AND is_deleted = 0")
    int updateById(EmployeeDO record);

    @Update("UPDATE emp_employee SET is_deleted = 1, gmt_modified = NOW() "
            + "WHERE tenant_id = #{tenantId} AND emp_no = #{empNo} AND is_deleted = 0")
    int softDelete(@Param("tenantId") String tenantId, @Param("empNo") String empNo);

    @Select("<script>"
            + "SELECT COUNT(1) FROM emp_employee WHERE tenant_id = #{tenantId} AND is_deleted = 0 "
            + "<if test='keyword != null and keyword != \"\"'>AND (emp_no LIKE CONCAT('%', #{keyword}, '%') "
            + "OR emp_name LIKE CONCAT('%', #{keyword}, '%'))</if>"
            + "<if test='deptName != null and deptName != \"\"'>AND dept_name = #{deptName}</if>"
            + "<if test='status != null and status != \"\"'>AND status = #{status}</if>"
            + "</script>")
    long countPage(@Param("tenantId") String tenantId, @Param("keyword") String keyword,
                   @Param("deptName") String deptName, @Param("status") String status);

    @Select("<script>"
            + "SELECT * FROM emp_employee WHERE tenant_id = #{tenantId} AND is_deleted = 0 "
            + "<if test='keyword != null and keyword != \"\"'>AND (emp_no LIKE CONCAT('%', #{keyword}, '%') "
            + "OR emp_name LIKE CONCAT('%', #{keyword}, '%'))</if>"
            + "<if test='deptName != null and deptName != \"\"'>AND dept_name = #{deptName}</if>"
            + "<if test='status != null and status != \"\"'>AND status = #{status}</if>"
            + "ORDER BY id DESC LIMIT #{offset}, #{pageSize}"
            + "</script>")
    List<EmployeeDO> selectPage(@Param("tenantId") String tenantId, @Param("keyword") String keyword,
                                @Param("deptName") String deptName, @Param("status") String status,
                                @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(1) FROM emp_employee WHERE tenant_id = #{tenantId} AND is_deleted = 0 AND status = 'ACTIVE'")
    long countActive(@Param("tenantId") String tenantId);

    @Select("SELECT COUNT(DISTINCT dept_name) FROM emp_employee WHERE tenant_id = #{tenantId} AND is_deleted = 0")
    long countDepts(@Param("tenantId") String tenantId);
}

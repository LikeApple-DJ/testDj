package com.example.employeeboard.whitelist.mapper;

import com.example.employeeboard.whitelist.WhitelistDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 白名单数据访问层。
 */
@Mapper
public interface WhitelistMapper {

    @Insert("INSERT INTO emp_whitelist (tenant_id, emp_no, user_name, remark, created_by) "
            + "VALUES (#{tenantId}, #{empNo}, #{userName}, #{remark}, #{createdBy})")
    int insert(WhitelistDO record);

    @Select("SELECT * FROM emp_whitelist WHERE tenant_id = #{tenantId} AND emp_no = #{empNo} AND is_deleted = 0")
    WhitelistDO selectByEmpNo(@Param("tenantId") String tenantId, @Param("empNo") String empNo);

    @Update("UPDATE emp_whitelist SET is_deleted = 1, gmt_modified = NOW() "
            + "WHERE tenant_id = #{tenantId} AND emp_no = #{empNo} AND is_deleted = 0")
    int softDelete(@Param("tenantId") String tenantId, @Param("empNo") String empNo);

    @Update("UPDATE emp_whitelist SET is_deleted = 0, user_name = #{userName}, remark = #{remark}, "
            + "gmt_modified = NOW() WHERE tenant_id = #{tenantId} AND emp_no = #{empNo}")
    int revive(WhitelistDO record);

    @Select("SELECT COUNT(1) FROM emp_whitelist WHERE tenant_id = #{tenantId} AND is_deleted = 0")
    long countActive(@Param("tenantId") String tenantId);

    @Select("SELECT COUNT(1) FROM emp_whitelist WHERE tenant_id = #{tenantId} AND is_deleted = 0 "
            + "AND (#{keyword} IS NULL OR emp_no LIKE CONCAT('%', #{keyword}, '%') "
            + "OR user_name LIKE CONCAT('%', #{keyword}, '%'))")
    long countPage(@Param("tenantId") String tenantId, @Param("keyword") String keyword);

    @Select("SELECT * FROM emp_whitelist WHERE tenant_id = #{tenantId} AND is_deleted = 0 "
            + "AND (#{keyword} IS NULL OR emp_no LIKE CONCAT('%', #{keyword}, '%') "
            + "OR user_name LIKE CONCAT('%', #{keyword}, '%')) "
            + "ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<WhitelistDO> selectPage(@Param("tenantId") String tenantId, @Param("keyword") String keyword,
                                 @Param("offset") int offset, @Param("pageSize") int pageSize);
}

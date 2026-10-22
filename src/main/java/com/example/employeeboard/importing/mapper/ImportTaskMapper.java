package com.example.employeeboard.importing.mapper;

import com.example.employeeboard.importing.ImportDetailDO;
import com.example.employeeboard.importing.ImportTaskDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 导入批次与明细数据访问层。
 */
@Mapper
public interface ImportTaskMapper {

    @Insert("INSERT INTO emp_import_task (tenant_id, operator, file_name, total_count, success_count, "
            + "fail_count, status) VALUES (#{tenantId}, #{operator}, #{fileName}, #{totalCount}, "
            + "#{successCount}, #{failCount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertTask(ImportTaskDO task);

    @Update("UPDATE emp_import_task SET total_count = #{totalCount}, success_count = #{successCount}, "
            + "fail_count = #{failCount}, status = #{status}, gmt_modified = NOW() WHERE id = #{id}")
    int updateTask(ImportTaskDO task);

    @Select("SELECT * FROM emp_import_task WHERE id = #{taskId}")
    ImportTaskDO selectTaskById(@Param("taskId") Long taskId);

    @Select("SELECT * FROM emp_import_task WHERE tenant_id = #{tenantId} ORDER BY id DESC LIMIT 1")
    ImportTaskDO selectLatestTask(@Param("tenantId") String tenantId);

    @Insert("INSERT INTO emp_import_detail (task_id, row_no, emp_no, result, fail_reason) "
            + "VALUES (#{taskId}, #{rowNo}, #{empNo}, #{result}, #{failReason})")
    int insertDetail(ImportDetailDO detail);

    @Select("SELECT * FROM emp_import_detail WHERE task_id = #{taskId} "
            + "AND (#{result} IS NULL OR result = #{result}) ORDER BY row_no")
    List<ImportDetailDO> selectDetails(@Param("taskId") Long taskId, @Param("result") String result);
}

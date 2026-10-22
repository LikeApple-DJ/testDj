package com.example.employeeboard.importing;

import org.springframework.web.multipart.MultipartFile;

/**
 * 批量导入服务。
 */
public interface ImportService {

    /**
     * 批量导入执行（S07），同步解析校验并落库。
     *
     * @param file      .xlsx 文件，≤1000 数据行
     * @param operator  操作人工号
     * @param overwrite 工号已存在时是否覆盖更新
     */
    ImportTaskDTO importEmployees(MultipartFile file, String operator, boolean overwrite);

    /**
     * 导入批次查询（S08），result 可选 SUCCESS/FAIL 过滤。
     */
    ImportTaskDTO getTaskDetail(Long taskId, String result);
}

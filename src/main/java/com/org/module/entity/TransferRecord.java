package com.org.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("employee_transfer_record")
public class TransferRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long employeeId;
    private Long oldDeptId;
    private Long newDeptId;
    private String oldPosition;
    private String newPosition;
    private String reason;
    private Long operatorId;
    private LocalDateTime createdAt;
}

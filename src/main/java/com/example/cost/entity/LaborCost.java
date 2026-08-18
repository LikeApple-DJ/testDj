package com.example.cost.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_labor_cost")
public class LaborCost {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long personnelId;
    private Long projectId;
    private Long businessLineId;
    private Long departmentId;
    private String role;
    private BigDecimal costAmount;
    private String costMonth;
    private String costQuarter;
    private String costYear;
    private LocalDateTime createdAt;
}
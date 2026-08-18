package com.example.cost.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_project_cost")
public class ProjectCost {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private Long departmentId;
    private Long businessLineId;
    private BigDecimal budgetAmount;
    private BigDecimal actualAmount;
    private String costMonth;
    private String costQuarter;
    private String costYear;
    private LocalDateTime createdAt;
}
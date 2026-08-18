package com.example.cost.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_project")
public class Project {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long departmentId;
    private Long businessLineId;
    private BigDecimal budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
}
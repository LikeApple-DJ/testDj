package com.example.cost.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_personnel")
public class Personnel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long departmentId;
    private String role;
    private BigDecimal monthlySalary;
    private LocalDateTime createdAt;
}
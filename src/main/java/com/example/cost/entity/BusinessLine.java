package com.example.cost.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_business_line")
public class BusinessLine {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long departmentId;
    private LocalDateTime createdAt;
}
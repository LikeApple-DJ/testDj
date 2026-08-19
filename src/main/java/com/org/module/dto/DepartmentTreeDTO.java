package com.org.module.dto;

import lombok.Data;
import java.util.List;

@Data
public class DepartmentTreeDTO {
    private Long id;
    private String name;
    private List<DepartmentTreeDTO> children;
}

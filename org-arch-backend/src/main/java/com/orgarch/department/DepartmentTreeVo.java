package com.orgarch.department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentTreeVo {
    private Long id;
    private String name;
    private Long parentId;
    private List<DepartmentTreeVo> children = new ArrayList<>();

    public DepartmentTreeVo() {}

    public DepartmentTreeVo(Long id, String name, Long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getParentId() { return parentId; }
    public List<DepartmentTreeVo> getChildren() { return children; }
}

package com.orgarch.department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "path", nullable = false, length = 512)
    private String path;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /** 部门主管员工ID（design 5.1.1.1 / 假设 A03），用于审批流推导与数据权限。 */
    @Column(name = "leader_id")
    private Long leaderId;

    @Column(name = "is_deleted", nullable = false)
    private Integer isDeleted = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Department() {}

    public Department(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
        this.path = "/";
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public Integer getSortOrder() { return sortOrder; }
    public Long getLeaderId() { return leaderId; }
    public void setLeaderId(Long leaderId) { this.leaderId = leaderId; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

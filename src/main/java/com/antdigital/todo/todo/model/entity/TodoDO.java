package com.antdigital.todo.todo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * 待办事项数据对象，映射 todo 表
 *
 * @author AiWork
 * @date 2026/08/31
 */
@Entity
@Table(name = "todo")
public class TodoDO {

    /** 系统自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 事项名称 */
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /** 事项描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 租户标识，预留隔离 */
    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    /** 创建时间 */
    @Column(name = "gmt_create", nullable = false)
    private LocalDateTime gmtCreate;

    /** 修改时间 */
    @Column(name = "gmt_modified", nullable = false)
    private LocalDateTime gmtModified;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        this.gmtCreate = now;
        this.gmtModified = now;
    }

    @PreUpdate
    void preUpdate() {
        this.gmtModified = LocalDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TodoDO todoDO = (TodoDO) o;
        return Objects.equals(id, todoDO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TodoDO{id=" + id + ", name='" + name + "', description='" + description
                + "', tenantId='" + tenantId + "', gmtCreate=" + gmtCreate
                + ", gmtModified=" + gmtModified + '}';
    }
}

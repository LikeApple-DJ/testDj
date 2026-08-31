package com.example.todo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 待办事项实体，映射 todo 表。
 *
 * <p>字段对应 design.md §5.2.1.1：id / name / description / creator_id / tenant_id / gmt_create / gmt_modified。</p>
 */
@Entity
@Table(name = "todo", indexes = {
        @Index(name = "idx_todo_creator", columnList = "creator_id"),
        @Index(name = "idx_todo_tenant", columnList = "tenant_id")
})
public class Todo {

    /** 系统自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 事项名称，必填，1~200 字符 */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /** 事项描述，选填，0~2000 字符 */
    @Column(name = "description", length = 2000)
    private String description;

    /** 创建人ID，取登录态用户ID */
    @Column(name = "creator_id")
    private Long creatorId;

    /** 租户ID，预留隔离，当前单租户默认 0 */
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId = 0L;

    /** 创建时间 */
    @Column(name = "gmt_create", nullable = false, updatable = false)
    private LocalDateTime gmtCreate;

    /** 修改时间 */
    @Column(name = "gmt_modified", nullable = false)
    private LocalDateTime gmtModified;

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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
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
    public String toString() {
        return "Todo{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", creatorId=" + creatorId
                + ", tenantId=" + tenantId
                + ", gmtCreate=" + gmtCreate
                + ", gmtModified=" + gmtModified
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Todo todo = (Todo) o;
        return Objects.equals(id, todo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

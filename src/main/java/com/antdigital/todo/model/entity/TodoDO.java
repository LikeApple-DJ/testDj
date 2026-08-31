package com.antdigital.todo.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 待办事项数据对象，对应 biz_todo 表。
 *
 * <p>对应 design.md §5.1.1.1 biz_todo 表结构。</p>
 * <p>注意：数据库字段 is_deleted 映射到 Java 属性 deleted（不加 is 前缀，遵从 mysql.md §4.2）。</p>
 */
public class TodoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 系统自增主键 */
    private Long id;

    /** 租户ID，逻辑隔离 */
    private String tenantId;

    /** 事项名称 */
    private String name;

    /** 事项描述 */
    private String description;

    /** 状态：0待处理/1进行中/2已完成 */
    private Integer status;

    /** 创建人（登录态用户标识） */
    private String creator;

    /** 是否删除：0否/1是 */
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime gmtCreate;

    /** 修改时间 */
    private LocalDateTime gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
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
        return "TodoDO{" +
                "id=" + id +
                ", tenantId='" + tenantId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", creator='" + creator + '\'' +
                ", deleted=" + deleted +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                '}';
    }
}

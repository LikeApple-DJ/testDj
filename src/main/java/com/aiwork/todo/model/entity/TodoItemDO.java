package com.aiwork.todo.model.entity;

import java.time.LocalDateTime;

/**
 * 待办事项数据对象，对应 todo_item 表
 *
 * @author AiWork
 * @date 2026/09/01
 */
public class TodoItemDO {

    /** 系统自增主键 */
    private Long id;

    /** 租户标识 */
    private String tenantId;

    /** 待办事项名称 */
    private String title;

    /** 待办事项描述 */
    private String description;

    /** 事项状态 */
    private String status;

    /** 创建人标识 */
    private String creator;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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
        return "TodoItemDO{"
                + "id=" + id
                + ", tenantId='" + tenantId + '\''
                + ", title='" + title + '\''
                + ", status='" + status + '\''
                + ", creator='" + creator + '\''
                + '}';
    }
}

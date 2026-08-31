package com.example.todo.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 新增待办事项创建结果。
 *
 * <p>对应 design.md §5.2.2 W01 出参 data：id / name / description / creatorId / gmtCreate。</p>
 */
public class TodoCreateResult {

    /** 待办事项ID */
    private Long id;

    /** 事项名称 */
    private String name;

    /** 事项描述 */
    private String description;

    /** 创建人ID */
    private Long creatorId;

    /** 创建时间 */
    private LocalDateTime gmtCreate;

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

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "TodoCreateResult{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", creatorId=" + creatorId
                + ", gmtCreate=" + gmtCreate
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
        TodoCreateResult that = (TodoCreateResult) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(creatorId, that.creatorId)
                && Objects.equals(gmtCreate, that.gmtCreate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, creatorId, gmtCreate);
    }
}

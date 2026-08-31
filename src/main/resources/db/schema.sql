-- =====================================================
-- biz_todo 待办事项表 DDL
-- 对应 design.md §5.1.1.1 表结构设计
-- =====================================================

CREATE TABLE IF NOT EXISTS biz_todo (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    tenant_id    VARCHAR(32)  NOT NULL DEFAULT ''     COMMENT '租户ID，逻辑隔离',
    name         VARCHAR(128) NOT NULL                COMMENT '事项名称',
    description  VARCHAR(1024) NULL DEFAULT NULL      COMMENT '事项描述',
    status       TINYINT      NOT NULL DEFAULT 0      COMMENT '状态：0待处理/1进行中/2已完成',
    creator      VARCHAR(64)  NOT NULL DEFAULT ''     COMMENT '创建人（登录态用户标识）',
    is_deleted    TINYINT      NOT NULL DEFAULT 0      COMMENT '是否删除：0否/1是',
    gmt_create   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY pk_biz_todo (id),
    UNIQUE KEY uk_biz_todo_tenant_name (tenant_id, name, is_deleted),
    KEY idx_biz_todo_tenant_status (tenant_id, status),
    KEY idx_biz_todo_creator (creator)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待办事项表';

-- 待办事项表
-- 最小闭环：仅创建，预留 tenant_id/creator/status 便于后续按人查询、多租户隔离与状态流转
CREATE TABLE IF NOT EXISTS todo_item (
    id           BIGINT       NOT NULL AUTO_INCREMENT          COMMENT '系统自增主键',
    tenant_id    VARCHAR(32)  NOT NULL DEFAULT ''              COMMENT '租户标识，多租户隔离，本期默认空串',
    title        VARCHAR(100) NOT NULL                         COMMENT '待办事项名称',
    description  VARCHAR(1000) NOT NULL DEFAULT ''              COMMENT '待办事项描述，本期选填，存空串',
    status       VARCHAR(16)  NOT NULL DEFAULT 'PENDING'       COMMENT '事项状态：PENDING-待处理、DONE-已完成、DELETED-已删除',
    creator      VARCHAR(64)  NOT NULL DEFAULT ''              COMMENT '创建人标识，由登录上下文带入',
    gmt_create   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP        COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY pk_todo_item (id),
    INDEX idx_todo_item_tenant_creator (tenant_id, creator),
    INDEX idx_todo_item_gmt_create (gmt_create)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '待办事项表';

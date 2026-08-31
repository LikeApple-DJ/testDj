-- 待办事项表
-- 遵循 MySQL 规约：表名小写下划线、必备 id/gmt_create/gmt_modified、datetime 非 timestamp
CREATE TABLE IF NOT EXISTS todo (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    name         VARCHAR(100) NOT NULL                COMMENT '事项名称',
    description  VARCHAR(500)          DEFAULT NULL    COMMENT '事项描述',
    tenant_id    VARCHAR(50)  NOT NULL DEFAULT 'default' COMMENT '租户标识，预留隔离',
    gmt_create   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    INDEX idx_todo_tenant (tenant_id),
    INDEX idx_todo_create (gmt_create)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '待办事项表';

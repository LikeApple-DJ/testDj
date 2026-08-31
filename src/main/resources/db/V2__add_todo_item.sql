-- 待办事项表
CREATE TABLE IF NOT EXISTS todo_item (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '系统自增主键',
    name         VARCHAR(128) NOT NULL COMMENT '事项名称',
    description  VARCHAR(1024) DEFAULT NULL COMMENT '事项描述',
    creator      VARCHAR(64) DEFAULT NULL COMMENT '创建人标识',
    is_deleted   TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    gmt_create   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    INDEX idx_todo_item_creator (creator),
    INDEX idx_todo_item_gmt_create (gmt_create)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待办事项表';

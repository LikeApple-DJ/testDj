-- 待办事项表 DDL
CREATE TABLE IF NOT EXISTS todo_item (
    id           BIGINT       AUTO_INCREMENT         COMMENT '系统自增主键',
    title        VARCHAR(100) NOT NULL               COMMENT '待办事项名称',
    description  VARCHAR(500)          DEFAULT NULL   COMMENT '待办事项描述',
    creator      VARCHAR(64)  NOT NULL               COMMENT '创建人标识（用户ID/工号）',
    gmt_create   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY pk_todo_item (id),
    INDEX idx_todo_item_creator (creator)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '待办事项表';

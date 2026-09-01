-- 待办事项表
CREATE TABLE IF NOT EXISTS todo (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    title       VARCHAR(200) NOT NULL COMMENT '事项名称',
    description VARCHAR(1000) DEFAULT NULL COMMENT '事项描述',
    creator_id  BIGINT NOT NULL COMMENT '创建人ID（内部用户ID，来自登录上下文）',
    version     BIGINT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    is_deleted  TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0=否 1=是',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    INDEX idx_todo_creator (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待办事项表';

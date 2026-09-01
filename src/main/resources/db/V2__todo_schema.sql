-- 待办事项表
CREATE TABLE IF NOT EXISTS todo (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    title       VARCHAR(200) NOT NULL COMMENT '事项名称',
    description VARCHAR(1000) DEFAULT NULL COMMENT '事项描述',
    status      TINYINT DEFAULT 0 COMMENT '状态: 0=待处理, 1=已完成',
    version     BIGINT DEFAULT 0 COMMENT '乐观锁版本号',
    is_deleted  TINYINT DEFAULT 0 COMMENT '逻辑删除 0=否 1=是',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_todo_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待办事项表';

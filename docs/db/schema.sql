-- ============================================
-- 待办事项管理系统 - DDL
-- 数据库: MySQL (InnoDB)
-- ============================================

CREATE DATABASE IF NOT EXISTS todo_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE todo_db;

-- 待办事项表
CREATE TABLE IF NOT EXISTS todo_item (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id     VARCHAR(64)  NOT NULL                COMMENT '创建用户ID',
    title       VARCHAR(100) NOT NULL                COMMENT '事项名称',
    description VARCHAR(500) DEFAULT NULL            COMMENT '事项描述',
    status      TINYINT(4)   NOT NULL DEFAULT 0      COMMENT '状态：0-待办 1-已完成 2-已取消',
    gmt_create  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id) COMMENT 'pk_todo_item',
    INDEX idx_todo_item_user_id (user_id) COMMENT '用户ID索引',
    INDEX idx_todo_item_status (status) COMMENT '状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办事项表';
-- 初始化用户数据
INSERT INTO users (username, password, caller_id, display_name, caller_type, caller_level, caller_dept)
VALUES ('admin', 'admin123', 'user001', '张三', '正式', 'P7', '技术部');

INSERT INTO users (username, password, caller_id, display_name, caller_type, caller_level, caller_dept)
VALUES ('user1', 'pass123', 'user002', '李四', '正式', 'P6', '技术部');

INSERT INTO users (username, password, caller_id, display_name, caller_type, caller_level, caller_dept)
VALUES ('user2', 'pass123', 'user003', '王五', '外包', 'P5', '产品部');

INSERT INTO users (username, password, caller_id, display_name, caller_type, caller_level, caller_dept)
VALUES ('user3', 'pass123', 'user004', '赵六', '正式', 'P8', '运营部');

INSERT INTO users (username, password, caller_id, display_name, caller_type, caller_level, caller_dept)
VALUES ('user4', 'pass123', 'user005', '陈七', '实习生', 'P5', '技术部');

INSERT INTO users (username, password, caller_id, display_name, caller_type, caller_level, caller_dept)
VALUES ('user5', 'pass123', 'user006', '刘八', '外包', 'P6', '产品部');

INSERT INTO users (username, password, caller_id, display_name, caller_type, caller_level, caller_dept)
VALUES ('user6', 'pass123', 'user007', '周九', '正式', 'P7', '运营部');

INSERT INTO users (username, password, caller_id, display_name, caller_type, caller_level, caller_dept)
VALUES ('user7', 'pass123', 'user008', '吴十', '正式', 'P6', '技术部');

-- 初始化模拟调用记录（用于统计图表展示）
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user001', '正式', 'P7', '技术部', 'hello', '2024-12-01T10:00:00', 15);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user002', '正式', 'P6', '技术部', 'hash', '2024-12-01T10:05:00', 23);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user003', '外包', 'P5', '产品部', 'sort/bubble', '2024-12-01T10:10:00', 45);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user004', '正式', 'P8', '运营部', 'hello', '2024-12-01T11:00:00', 12);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user005', '实习生', 'P5', '技术部', 'hash', '2024-12-01T11:30:00', 30);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user001', '正式', 'P7', '技术部', 'sort/bubble', '2024-12-01T14:00:00', 52);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user006', '外包', 'P6', '产品部', 'hello', '2024-12-02T09:00:00', 18);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user007', '正式', 'P7', '运营部', 'hash', '2024-12-02T09:30:00', 25);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user008', '正式', 'P6', '技术部', 'sort/bubble', '2024-12-02T10:00:00', 38);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user002', '正式', 'P6', '技术部', 'hello', '2024-12-02T11:00:00', 14);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user003', '外包', 'P5', '产品部', 'hash', '2024-12-02T14:00:00', 28);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user005', '实习生', 'P5', '技术部', 'sort/bubble', '2024-12-02T15:00:00', 42);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user004', '正式', 'P8', '运营部', 'hash', '2024-12-03T10:00:00', 22);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user006', '外包', 'P6', '产品部', 'sort/bubble', '2024-12-03T11:00:00', 35);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user001', '正式', 'P7', '技术部', 'hash', '2024-12-03T14:00:00', 20);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user007', '正式', 'P7', '运营部', 'hello', '2024-12-03T15:30:00', 16);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user008', '正式', 'P6', '技术部', 'hash', '2024-12-04T09:00:00', 26);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user003', '外包', 'P5', '产品部', 'hello', '2024-12-04T10:00:00', 13);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user005', '实习生', 'P5', '技术部', 'hello', '2024-12-04T11:00:00', 17);
INSERT INTO call_records (caller_id, caller_type, caller_level, caller_dept, api_name, call_time, response_time)
VALUES ('user002', '正式', 'P6', '技术部', 'sort/bubble', '2024-12-04T14:00:00', 48);
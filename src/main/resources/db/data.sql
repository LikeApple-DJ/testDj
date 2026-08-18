-- 部门
INSERT INTO t_department (name) VALUES ('技术部'), ('产品部'), ('运营部');

-- 业务线
INSERT INTO t_business_line (name, department_id) VALUES
('核心平台', 1), ('数据服务', 1), ('用户产品', 2), ('内容运营', 3);

-- 项目
INSERT INTO t_project (name, department_id, business_line_id, budget) VALUES
('平台升级项目', 1, 1, 500000.00),
('数据分析平台', 1, 2, 300000.00),
('用户增长项目', 2, 3, 200000.00);

-- 人员
INSERT INTO t_personnel (name, department_id, role, monthly_salary) VALUES
('张三', 1, 'dev', 25000.00),
('李四', 1, 'dev', 22000.00),
('王五', 2, 'product', 28000.00),
('赵六', 1, 'test', 20000.00),
('钱七', 1, 'ops', 23000.00);

-- 人力成本
INSERT INTO t_labor_cost (personnel_id, project_id, business_line_id, department_id, role, cost_amount, cost_month, cost_quarter, cost_year) VALUES
(1, 1, 1, 1, 'dev', 25000.00, '2026-08', '2026-Q3', '2026'),
(2, 1, 1, 1, 'dev', 22000.00, '2026-08', '2026-Q3', '2026'),
(4, 1, 1, 1, 'test', 20000.00, '2026-08', '2026-Q3', '2026'),
(5, 1, 1, 1, 'ops', 23000.00, '2026-08', '2026-Q3', '2026'),
(3, 3, 3, 2, 'product', 28000.00, '2026-08', '2026-Q3', '2026');

-- 项目成本
INSERT INTO t_project_cost (project_id, department_id, business_line_id, budget_amount, actual_amount, cost_month, cost_quarter, cost_year) VALUES
(1, 1, 1, 500000.00, 120000.00, '2026-08', '2026-Q3', '2026'),
(2, 1, 2, 300000.00, 85000.00, '2026-08', '2026-Q3', '2026'),
(3, 2, 3, 200000.00, 45000.00, '2026-08', '2026-Q3', '2026');
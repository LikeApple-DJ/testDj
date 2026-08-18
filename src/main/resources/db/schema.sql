DROP TABLE IF EXISTS t_labor_cost;
DROP TABLE IF EXISTS t_project_cost;
DROP TABLE IF EXISTS t_personnel;
DROP TABLE IF EXISTS t_project;
DROP TABLE IF EXISTS t_business_line;
DROP TABLE IF EXISTS t_department;

CREATE TABLE t_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE t_business_line (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES t_department(id)
);

CREATE TABLE t_project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    department_id BIGINT NOT NULL,
    business_line_id BIGINT NOT NULL,
    budget DECIMAL(15,2) NOT NULL DEFAULT 0,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES t_department(id),
    FOREIGN KEY (business_line_id) REFERENCES t_business_line(id)
);

CREATE TABLE t_personnel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    department_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'dev/test/product/ops',
    monthly_salary DECIMAL(12,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES t_department(id)
);

CREATE TABLE t_labor_cost (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    personnel_id BIGINT NOT NULL,
    project_id BIGINT,
    business_line_id BIGINT,
    department_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    cost_amount DECIMAL(12,2) NOT NULL,
    cost_month VARCHAR(7) NOT NULL COMMENT 'yyyy-MM',
    cost_quarter VARCHAR(7) NOT NULL COMMENT 'yyyy-Qn',
    cost_year VARCHAR(4) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (personnel_id) REFERENCES t_personnel(id),
    FOREIGN KEY (project_id) REFERENCES t_project(id),
    FOREIGN KEY (business_line_id) REFERENCES t_business_line(id),
    FOREIGN KEY (department_id) REFERENCES t_department(id)
);

CREATE TABLE t_project_cost (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    business_line_id BIGINT NOT NULL,
    budget_amount DECIMAL(15,2) NOT NULL,
    actual_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    cost_month VARCHAR(7) NOT NULL,
    cost_quarter VARCHAR(7) NOT NULL,
    cost_year VARCHAR(4) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES t_project(id),
    FOREIGN KEY (department_id) REFERENCES t_department(id),
    FOREIGN KEY (business_line_id) REFERENCES t_business_line(id)
);
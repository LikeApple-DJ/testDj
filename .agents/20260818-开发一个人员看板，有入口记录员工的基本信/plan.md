# 人员看板 (Personnel Dashboard) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a full-stack personnel dashboard with employee basic info CRUD, batch import (CSV/Excel), cost budget tracking, and whitelist mechanism across two repositories.

**Architecture:** Two-repo split — `testDj-main` hosts the frontend (Vue 3 + Element Plus) serving as the personnel dashboard UI; `testDJnew-main` hosts the backend (Java Spring Boot REST API + MySQL) providing data persistence, file upload processing, and authorization. Communication via RESTful JSON API over HTTP.

**Tech Stack:**
- Frontend: Vue 3 + TypeScript + Vite + Element Plus + Axios + XLSX (SheetJS) for Excel parsing
- Backend: Java 17 + Spring Boot 3.x + Spring Data JPA + MySQL 8 + Apache POI (Excel) + OpenCSV (CSV)
- Cross-repo: REST API contract defined in `api-contract.yaml` (OpenAPI 3.0)

---

## Global Constraints

- Java 17 minimum, Spring Boot 3.2+, Vue 3.4+
- All API endpoints prefixed with `/api/v1/`
- Frontend must use TypeScript for all `.vue` and `.ts` files
- Database: MySQL 8, charset `utf8mb4`, collation `utf8mb4_unicode_ci`
- No external cloud services; all processing self-contained
- Import file size limit: 10MB per file
- All date/time fields use ISO-8601 format (`yyyy-MM-dd'T'HH:mm:ss`)
- Whitelist enforced server-side; frontend only hides UI elements
- **Git write operations are prohibited** — all commits must be done outside this session

---

## Repository Roles

| Repository | Role | Base Path |
|---|---|---|
| `testDj-main` | Frontend — Personnel Dashboard UI | `/workspace/worktree/testDj-main` |
| `testDJnew-main` | Backend — REST API Server | `/workspace/worktree/testDJnew-main` |

---

## API Contract (Cross-Repo Interface)

All tasks below reference these shared interfaces. Both repos must align on these signatures.

### Employee Endpoints

| Method | Path | Request | Response |
|---|---|---|---|
| GET | `/api/v1/employees` | `?page=0&size=20&search=&department=` | `{ content: Employee[], totalElements: number, totalPages: number }` |
| GET | `/api/v1/employees/{id}` | — | `Employee` |
| POST | `/api/v1/employees` | `EmployeeCreateRequest` | `Employee` |
| PUT | `/api/v1/employees/{id}` | `EmployeeUpdateRequest` | `Employee` |
| DELETE | `/api/v1/employees/{id}` | — | `204 No Content` |
| POST | `/api/v1/employees/import` | `multipart/form-data file` | `ImportResult` |

### Whitelist Endpoints

| Method | Path | Request | Response |
|---|---|---|---|
| GET | `/api/v1/whitelist/import` | — | `WhitelistEntry[]` |
| POST | `/api/v1/whitelist/import` | `WhitelistEntryCreateRequest` | `WhitelistEntry` |
| DELETE | `/api/v1/whitelist/import/{id}` | — | `204 No Content` |
| GET | `/api/v1/whitelist/permission` | — | `PermissionWhitelistEntry[]` |
| POST | `/api/v1/whitelist/permission` | `PermissionWhitelistEntryCreateRequest` | `PermissionWhitelistEntry` |
| DELETE | `/api/v1/whitelist/permission/{id}` | — | `204 No Content` |

### Cost Budget Endpoints

| Method | Path | Request | Response |
|---|---|---|---|
| GET | `/api/v1/employees/{id}/costs` | `?year=2025` | `CostBudget[]` |
| POST | `/api/v1/employees/{id}/costs` | `CostBudgetCreateRequest` | `CostBudget` |
| PUT | `/api/v1/employees/{id}/costs/{costId}` | `CostBudgetUpdateRequest` | `CostBudget` |
| DELETE | `/api/v1/employees/{id}/costs/{costId}` | — | `204 No Content` |

### Shared Data Models

```typescript
// Frontend TypeScript (mirrors backend Java DTOs)
interface Employee {
  id: number;
  name: string;
  employeeNo: string;
  department: string;
  position: string;
  phone: string;
  email: string;
  hireDate: string;           // ISO-8601 date
  salary: number;
  bankAccount: string;
  education: string;
  skills: string;
  contractEndDate: string;
  address: string;
  emergencyContact: string;
  emergencyPhone: string;
  createdAt: string;
  updatedAt: string;
}

interface EmployeeCreateRequest {
  name: string;
  employeeNo: string;
  department: string;
  position: string;
  phone: string;
  email: string;
  hireDate: string;
  salary?: number;
  bankAccount?: string;
  education?: string;
  skills?: string;
  contractEndDate?: string;
  address?: string;
  emergencyContact?: string;
  emergencyPhone?: string;
}

type EmployeeUpdateRequest = EmployeeCreateRequest;

interface ImportResult {
  totalRows: number;
  successRows: number;
  failedRows: number;
  errors: ImportError[];
}

interface ImportError {
  row: number;
  column: string;
  message: string;
}

interface WhitelistEntry {
  id: number;
  department: string;
  allowedAction: string;    // "IMPORT" | "EXPORT"
  createdBy: string;
  createdAt: string;
}

interface WhitelistEntryCreateRequest {
  department: string;
  allowedAction: string;
}

interface PermissionWhitelistEntry {
  id: number;
  userId: string;
  permission: string;       // "VIEW_COST" | "EDIT_COST" | "VIEW_ALL"
  createdBy: string;
  createdAt: string;
}

interface PermissionWhitelistEntryCreateRequest {
  userId: string;
  permission: string;
}

interface CostBudget {
  id: number;
  employeeId: number;
  costType: string;         // "SALARY" | "TRAINING" | "TRAVEL" | "OTHER"
  amount: number;
  description: string;
  year: number;
  createdAt: string;
  updatedAt: string;
}

interface CostBudgetCreateRequest {
  costType: string;
  amount: number;
  description: string;
  year: number;
}

type CostBudgetUpdateRequest = CostBudgetCreateRequest;
```

---

## Task 1: [testDJnew-main] Backend — Project Scaffolding & Database Schema

**Files:**
- Create: `testDJnew-main/pom.xml`
- Create: `testDJnew-main/src/main/java/com/personnel/PersonnelApplication.java`
- Create: `testDJnew-main/src/main/resources/application.yml`
- Create: `testDJnew-main/src/main/resources/db/migration/V1__init_schema.sql`
- Create: `testDJnew-main/src/main/java/com/personnel/config/WebConfig.java`
- Test: `testDJnew-main/src/test/java/com/personnel/PersonnelApplicationTests.java`

**Interfaces:**
- Consumes: (nothing — this is the foundation task)
- Produces: Spring Boot project skeleton, database schema, base package structure

- [ ] **Step 1: Create `pom.xml` with Spring Boot 3.2, JPA, MySQL, Apache POI, OpenCSV, Flyway dependencies**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>
    <groupId>com.personnel</groupId>
    <artifactId>personnel-dashboard-backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>Personnel Dashboard Backend</name>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-mysql</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.5</version>
        </dependency>
        <dependency>
            <groupId>com.opencsv</groupId>
            <artifactId>opencsv</artifactId>
            <version>5.9</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: Create `application.yml`**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/personnel_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8mb4
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

spring:
  config:
    activate:
      on-profile: test
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    database-platform: org.hibernate.dialect.H2Dialect
  flyway:
    enabled: false
```

- [ ] **Step 3: Create Flyway migration `V1__init_schema.sql`**

```sql
CREATE TABLE employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    employee_no VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    position VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    hire_date DATE NOT NULL,
    salary DECIMAL(12,2),
    bank_account VARCHAR(50),
    education VARCHAR(100),
    skills TEXT,
    contract_end_date DATE,
    address VARCHAR(255),
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_department (department),
    INDEX idx_employee_no (employee_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE import_whitelist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(100) NOT NULL,
    allowed_action VARCHAR(50) NOT NULL,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_department_action (department, allowed_action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE permission_whitelist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    permission VARCHAR(50) NOT NULL,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_permission (user_id, permission)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cost_budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    cost_type VARCHAR(50) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    description VARCHAR(500),
    year INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    INDEX idx_employee_year (employee_id, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

- [ ] **Step 4: Create `PersonnelApplication.java`**

```java
package com.personnel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonnelApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonnelApplication.class, args);
    }
}
```

- [ ] **Step 5: Create `WebConfig.java` for CORS**

```java
package com.personnel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

- [ ] **Step 6: Create `PersonnelApplicationTests.java`**

```java
package com.personnel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PersonnelApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 7: Verify project compiles**

Run: `cd /workspace/worktree/testDJnew-main && mvn compile -q`
Expected: BUILD SUCCESS (no errors)

---

## Task 2: [testDJnew-main] Backend — Employee Entity & Repository

**Files:**
- Create: `testDJnew-main/src/main/java/com/personnel/entity/Employee.java`
- Create: `testDJnew-main/src/main/java/com/personnel/repository/EmployeeRepository.java`
- Test: `testDJnew-main/src/test/java/com/personnel/repository/EmployeeRepositoryTest.java`

**Interfaces:**
- Consumes: Task 1 schema (employees table)
- Produces: `Employee` JPA entity, `EmployeeRepository` with pagination/search

- [ ] **Step 1: Create `Employee.java` entity**

```java
package com.personnel.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "employee_no", nullable = false, unique = true, length = 50)
    private String employeeNo;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(length = 100)
    private String position;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    @Column(length = 100)
    private String education;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(length = 255)
    private String address;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 2: Create `EmployeeRepository.java`**

```java
package com.personnel.repository;

import com.personnel.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findByNameContainingOrEmployeeNoContaining(String name, String employeeNo, Pageable pageable);
    Page<Employee> findByDepartment(String department, Pageable pageable);
    Page<Employee> findByNameContainingOrEmployeeNoContainingAndDepartment(
            String name, String employeeNo, String department, Pageable pageable);
    boolean existsByEmployeeNo(String employeeNo);
}
```

- [ ] **Step 3: Create `EmployeeRepositoryTest.java`**

```java
package com.personnel.repository;

import com.personnel.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void shouldSaveAndFindEmployee() {
        Employee emp = Employee.builder()
                .name("张三")
                .employeeNo("EMP001")
                .department("技术部")
                .position("高级工程师")
                .phone("13800138000")
                .hireDate(LocalDate.of(2020, 1, 1))
                .salary(new BigDecimal("15000.00"))
                .build();
        employeeRepository.save(emp);

        Page<Employee> result = employeeRepository
                .findByNameContainingOrEmployeeNoContaining("张三", "", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmployeeNo()).isEqualTo("EMP001");
    }

    @Test
    void shouldReturnUniqueEmployeeNo() {
        Employee emp1 = Employee.builder()
                .name("李四")
                .employeeNo("EMP002")
                .department("财务部")
                .position("会计")
                .phone("13900139000")
                .hireDate(LocalDate.of(2021, 6, 1))
                .build();
        employeeRepository.save(emp1);

        boolean exists = employeeRepository.existsByEmployeeNo("EMP002");
        assertThat(exists).isTrue();

        boolean notExists = employeeRepository.existsByEmployeeNo("EMP999");
        assertThat(notExists).isFalse();
    }
}
```

- [ ] **Step 4: Run tests**

Run: `cd /workspace/worktree/testDJnew-main && mvn test -Dtest=EmployeeRepositoryTest -q`
Expected: Tests PASS (green)

---

## Task 3: [testDJnew-main] Backend — Employee DTOs & Service Layer

**Files:**
- Create: `testDJnew-main/src/main/java/com/personnel/dto/EmployeeCreateRequest.java`
- Create: `testDJnew-main/src/main/java/com/personnel/dto/EmployeeUpdateRequest.java`
- Create: `testDJnew-main/src/main/java/com/personnel/dto/EmployeeResponse.java`
- Create: `testDJnew-main/src/main/java/com/personnel/dto/PageResponse.java`
- Create: `testDJnew-main/src/main/java/com/personnel/service/EmployeeService.java`
- Create: `testDJnew-main/src/main/java/com/personnel/service/EmployeeServiceImpl.java`
- Test: `testDJnew-main/src/test/java/com/personnel/service/EmployeeServiceTest.java`

**Interfaces:**
- Consumes: `EmployeeRepository`, `Employee` entity
- Produces: `EmployeeService` interface with full CRUD + pagination + search

- [ ] **Step 1: Create `EmployeeCreateRequest.java`**

```java
package com.personnel.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeCreateRequest {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "工号不能为空")
    private String employeeNo;

    @NotBlank(message = "部门不能为空")
    private String department;

    private String position;
    private String phone;
    private String email;

    @NotNull(message = "入职日期不能为空")
    private LocalDate hireDate;

    private BigDecimal salary;
    private String bankAccount;
    private String education;
    private String skills;
    private LocalDate contractEndDate;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
}
```

- [ ] **Step 2: Create `EmployeeUpdateRequest.java`** (same fields as CreateRequest)

```java
package com.personnel.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeUpdateRequest {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "工号不能为空")
    private String employeeNo;

    @NotBlank(message = "部门不能为空")
    private String department;

    private String position;
    private String phone;
    private String email;

    @NotNull(message = "入职日期不能为空")
    private LocalDate hireDate;

    private BigDecimal salary;
    private String bankAccount;
    private String education;
    private String skills;
    private LocalDate contractEndDate;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
}
```

- [ ] **Step 3: Create `EmployeeResponse.java`**

```java
package com.personnel.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeResponse {
    private Long id;
    private String name;
    private String employeeNo;
    private String department;
    private String position;
    private String phone;
    private String email;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String bankAccount;
    private String education;
    private String skills;
    private LocalDate contractEndDate;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 4: Create `PageResponse.java`**

```java
package com.personnel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
```

- [ ] **Step 5: Create `EmployeeService.java` interface**

```java
package com.personnel.service;

import com.personnel.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
    PageResponse<EmployeeResponse> listEmployees(int page, int size, String search, String department);
    EmployeeResponse getEmployee(Long id);
    EmployeeResponse createEmployee(EmployeeCreateRequest request);
    EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request);
    void deleteEmployee(Long id);
    ImportResult importEmployees(MultipartFile file);
}
```

- [ ] **Step 6: Create `EmployeeServiceImpl.java`**

```java
package com.personnel.service;

import com.personnel.dto.*;
import com.personnel.entity.Employee;
import com.personnel.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final FileImportService fileImportService;

    @Override
    public PageResponse<EmployeeResponse> listEmployees(int page, int size, String search, String department) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Employee> employeePage;

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasDepartment = department != null && !department.isBlank();

        if (hasSearch && hasDepartment) {
            employeePage = employeeRepository.findByNameContainingOrEmployeeNoContainingAndDepartment(
                    search, search, department, pageable);
        } else if (hasSearch) {
            employeePage = employeeRepository.findByNameContainingOrEmployeeNoContaining(search, search, pageable);
        } else if (hasDepartment) {
            employeePage = employeeRepository.findByDepartment(department, pageable);
        } else {
            employeePage = employeeRepository.findAll(pageable);
        }

        List<EmployeeResponse> content = employeePage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<EmployeeResponse>builder()
                .content(content)
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .currentPage(page)
                .pageSize(size)
                .build();
    }

    @Override
    public EmployeeResponse getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("员工不存在: " + id));
        return toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.existsByEmployeeNo(request.getEmployeeNo())) {
            throw new RuntimeException("工号已存在: " + request.getEmployeeNo());
        }
        Employee employee = Employee.builder()
                .name(request.getName())
                .employeeNo(request.getEmployeeNo())
                .department(request.getDepartment())
                .position(request.getPosition())
                .phone(request.getPhone())
                .email(request.getEmail())
                .hireDate(request.getHireDate())
                .salary(request.getSalary())
                .bankAccount(request.getBankAccount())
                .education(request.getEducation())
                .skills(request.getSkills())
                .contractEndDate(request.getContractEndDate())
                .address(request.getAddress())
                .emergencyContact(request.getEmergencyContact())
                .emergencyPhone(request.getEmergencyPhone())
                .build();
        employee = employeeRepository.save(employee);
        return toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("员工不存在: " + id));
        employee.setName(request.getName());
        employee.setEmployeeNo(request.getEmployeeNo());
        employee.setDepartment(request.getDepartment());
        employee.setPosition(request.getPosition());
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setHireDate(request.getHireDate());
        employee.setSalary(request.getSalary());
        employee.setBankAccount(request.getBankAccount());
        employee.setEducation(request.getEducation());
        employee.setSkills(request.getSkills());
        employee.setContractEndDate(request.getContractEndDate());
        employee.setAddress(request.getAddress());
        employee.setEmergencyContact(request.getEmergencyContact());
        employee.setEmergencyPhone(request.getEmergencyPhone());
        employee = employeeRepository.save(employee);
        return toResponse(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("员工不存在: " + id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ImportResult importEmployees(MultipartFile file) {
        return fileImportService.importFile(file);
    }

    private EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .employeeNo(employee.getEmployeeNo())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .phone(employee.getPhone())
                .email(employee.getEmail())
                .hireDate(employee.getHireDate())
                .salary(employee.getSalary())
                .bankAccount(employee.getBankAccount())
                .education(employee.getEducation())
                .skills(employee.getSkills())
                .contractEndDate(employee.getContractEndDate())
                .address(employee.getAddress())
                .emergencyContact(employee.getEmergencyContact())
                .emergencyPhone(employee.getEmergencyPhone())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
```

- [ ] **Step 7: Create `EmployeeServiceTest.java`**

```java
package com.personnel.service;

import com.personnel.dto.EmployeeCreateRequest;
import com.personnel.dto.EmployeeResponse;
import com.personnel.dto.PageResponse;
import com.personnel.entity.Employee;
import com.personnel.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    void shouldCreateAndListEmployee() {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setName("王五");
        request.setEmployeeNo("EMP003");
        request.setDepartment("市场部");
        request.setPosition("经理");
        request.setHireDate(LocalDate.of(2022, 3, 15));

        EmployeeResponse created = employeeService.createEmployee(request);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("王五");

        PageResponse<EmployeeResponse> page = employeeService.listEmployees(0, 10, "", "");
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldSearchEmployeeByName() {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setName("赵六");
        request.setEmployeeNo("EMP004");
        request.setDepartment("研发部");
        request.setPosition("工程师");
        request.setHireDate(LocalDate.of(2023, 1, 1));
        employeeService.createEmployee(request);

        PageResponse<EmployeeResponse> page = employeeService.listEmployees(0, 10, "赵六", "");
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldDeleteEmployee() {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setName("孙七");
        request.setEmployeeNo("EMP005");
        request.setDepartment("人事部");
        request.setPosition("专员");
        request.setHireDate(LocalDate.of(2023, 6, 1));
        EmployeeResponse created = employeeService.createEmployee(request);

        employeeService.deleteEmployee(created.getId());

        PageResponse<EmployeeResponse> page = employeeService.listEmployees(0, 10, "", "");
        assertThat(page.getTotalElements()).isEqualTo(0);
    }
}
```

- [ ] **Step 8: Run tests**

Run: `cd /workspace/worktree/testDJnew-main && mvn test -Dtest=EmployeeServiceTest -q`
Expected: Tests PASS

---

## Task 4: [testDJnew-main] Backend — File Import Service (CSV + Excel)

**Files:**
- Create: `testDJnew-main/src/main/java/com/personnel/service/FileImportService.java`
- Create: `testDJnew-main/src/main/java/com/personnel/service/FileImportServiceImpl.java`
- Create: `testDJnew-main/src/main/java/com/personnel/dto/ImportResult.java`
- Create: `testDJnew-main/src/main/java/com/personnel/dto/ImportError.java`
- Test: `testDJnew-main/src/test/java/com/personnel/service/FileImportServiceTest.java`

**Interfaces:**
- Consumes: `EmployeeRepository`, `Employee` entity
- Produces: `FileImportService` with `importFile(MultipartFile)` → `ImportResult`

- [ ] **Step 1: Create `ImportResult.java`**

```java
package com.personnel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult {
    private int totalRows;
    private int successRows;
    private int failedRows;
    private List<ImportError> errors;
}
```

- [ ] **Step 2: Create `ImportError.java`**

```java
package com.personnel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportError {
    private int row;
    private String column;
    private String message;
}
```

- [ ] **Step 3: Create `FileImportService.java` interface**

```java
package com.personnel.service;

import com.personnel.dto.ImportResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileImportService {
    ImportResult importFile(MultipartFile file);
}
```

- [ ] **Step 4: Create `FileImportServiceImpl.java`**

```java
package com.personnel.service;

import com.personnel.dto.ImportError;
import com.personnel.dto.ImportResult;
import com.personnel.entity.Employee;
import com.personnel.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileImportServiceImpl implements FileImportService {

    private final EmployeeRepository employeeRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional
    public ImportResult importFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new RuntimeException("文件名不能为空");
        }

        if (filename.endsWith(".csv")) {
            return importCsv(file);
        } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            return importExcel(file);
        } else {
            throw new RuntimeException("不支持的文件格式，请上传 CSV 或 Excel 文件");
        }
    }

    private ImportResult importCsv(MultipartFile file) {
        List<ImportError> errors = new ArrayList<>();
        int totalRows = 0;
        int successRows = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = reader.readLine(); // skip header
            if (headerLine == null) {
                return ImportResult.builder().totalRows(0).successRows(0).failedRows(0).errors(errors).build();
            }

            String line;
            int rowNum = 1;
            while ((line = reader.readLine()) != null) {
                rowNum++;
                totalRows++;
                try {
                    String[] fields = parseCsvLine(line);
                    Employee employee = buildEmployeeFromFields(fields, rowNum, errors);
                    if (employee != null) {
                        employeeRepository.save(employee);
                        successRows++;
                    } else {
                        // error already added in buildEmployeeFromFields
                    }
                } catch (Exception e) {
                    errors.add(ImportError.builder()
                            .row(rowNum)
                            .column("ALL")
                            .message("解析失败: " + e.getMessage())
                            .build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("文件读取失败: " + e.getMessage());
        }

        return ImportResult.builder()
                .totalRows(totalRows)
                .successRows(successRows)
                .failedRows(totalRows - successRows)
                .errors(errors)
                .build();
    }

    private ImportResult importExcel(MultipartFile file) {
        List<ImportError> errors = new ArrayList<>();
        int totalRows = 0;
        int successRows = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                return ImportResult.builder().totalRows(0).successRows(0).failedRows(0).errors(errors).build();
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                totalRows++;
                try {
                    String[] fields = new String[15];
                    for (int j = 0; j < 15; j++) {
                        Cell cell = row.getCell(j);
                        fields[j] = getCellValueAsString(cell);
                    }
                    Employee employee = buildEmployeeFromFields(fields, i + 1, errors);
                    if (employee != null) {
                        employeeRepository.save(employee);
                        successRows++;
                    }
                } catch (Exception e) {
                    errors.add(ImportError.builder()
                            .row(i + 1)
                            .column("ALL")
                            .message("解析失败: " + e.getMessage())
                            .build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("文件读取失败: " + e.getMessage());
        }

        return ImportResult.builder()
                .totalRows(totalRows)
                .successRows(successRows)
                .failedRows(totalRows - successRows)
                .errors(errors)
                .build();
    }

    private Employee buildEmployeeFromFields(String[] fields, int rowNum, List<ImportError> errors) {
        if (fields.length < 5) {
            errors.add(ImportError.builder()
                    .row(rowNum)
                    .column("ALL")
                    .message("字段不足，至少需要5列")
                    .build());
            return null;
        }

        String name = fields[0];
        String employeeNo = fields[1];
        String department = fields[2];

        if (name == null || name.isBlank()) {
            errors.add(ImportError.builder().row(rowNum).column("姓名").message("姓名不能为空").build());
            return null;
        }
        if (employeeNo == null || employeeNo.isBlank()) {
            errors.add(ImportError.builder().row(rowNum).column("工号").message("工号不能为空").build());
            return null;
        }
        if (department == null || department.isBlank()) {
            errors.add(ImportError.builder().row(rowNum).column("部门").message("部门不能为空").build());
            return null;
        }

        if (employeeRepository.existsByEmployeeNo(employeeNo.trim())) {
            errors.add(ImportError.builder().row(rowNum).column("工号").message("工号已存在: " + employeeNo).build());
            return null;
        }

        LocalDate hireDate;
        try {
            hireDate = LocalDate.parse(fields[4].trim(), DATE_FORMATTER);
        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            errors.add(ImportError.builder().row(rowNum).column("入职日期").message("日期格式错误，需为 yyyy-MM-dd").build());
            return null;
        }

        Employee employee = Employee.builder()
                .name(name.trim())
                .employeeNo(employeeNo.trim())
                .department(department.trim())
                .position(safeGet(fields, 3))
                .hireDate(hireDate)
                .phone(safeGet(fields, 5))
                .email(safeGet(fields, 6))
                .salary(parseBigDecimal(safeGet(fields, 7)))
                .bankAccount(safeGet(fields, 8))
                .education(safeGet(fields, 9))
                .skills(safeGet(fields, 10))
                .address(safeGet(fields, 12))
                .emergencyContact(safeGet(fields, 13))
                .emergencyPhone(safeGet(fields, 14))
                .build();

        if (fields.length > 11 && fields[11] != null && !fields[11].isBlank()) {
            try {
                employee.setContractEndDate(LocalDate.parse(fields[11].trim(), DATE_FORMATTER));
            } catch (DateTimeParseException ignored) {
            }
        }

        return employee;
    }

    private String safeGet(String[] fields, int index) {
        if (index < fields.length && fields[index] != null) {
            String trimmed = fields[index].trim();
            return trimmed.isEmpty() ? null : trimmed;
        }
        return null;
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());
        return fields.toArray(new String[0]);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    yield String.valueOf((long) val);
                }
                yield String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }
}
```

- [ ] **Step 5: Create `FileImportServiceTest.java`**

```java
package com.personnel.service;

import com.personnel.dto.ImportResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class FileImportServiceTest {

    @Autowired
    private FileImportService fileImportService;

    @Test
    void shouldImportCsvSuccessfully() {
        String csv = "姓名,工号,部门,职位,入职日期,电话,邮箱\n" +
                     "张三,EMP100,技术部,工程师,2023-01-15,13800000001,zhangsan@test.com\n" +
                     "李四,EMP101,财务部,会计,2023-02-20,13800000002,lisi@test.com\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        ImportResult result = fileImportService.importFile(file);
        assertThat(result.getTotalRows()).isEqualTo(2);
        assertThat(result.getSuccessRows()).isEqualTo(2);
        assertThat(result.getFailedRows()).isEqualTo(0);
    }

    @Test
    void shouldReportImportErrors() {
        String csv = "姓名,工号,部门,职位,入职日期\n" +
                     ",EMP102,技术部,工程师,2023-01-15\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        ImportResult result = fileImportService.importFile(file);
        assertThat(result.getTotalRows()).isEqualTo(1);
        assertThat(result.getSuccessRows()).isEqualTo(0);
        assertThat(result.getFailedRows()).isEqualTo(1);
        assertThat(result.getErrors()).isNotEmpty();
    }
}
```

- [ ] **Step 6: Run tests**

Run: `cd /workspace/worktree/testDJnew-main && mvn test -Dtest=FileImportServiceTest -q`
Expected: Tests PASS

---

## Task 5: [testDJnew-main] Backend — Whitelist & Cost Budget Entities/Services

**Files:**
- Create: `testDJnew-main/src/main/java/com/personnel/entity/ImportWhitelist.java`
- Create: `testDJnew-main/src/main/java/com/personnel/entity/PermissionWhitelist.java`
- Create: `testDJnew-main/src/main/java/com/personnel/entity/CostBudget.java`
- Create: `testDJnew-main/src/main/java/com/personnel/repository/ImportWhitelistRepository.java`
- Create: `testDJnew-main/src/main/java/com/personnel/repository/PermissionWhitelistRepository.java`
- Create: `testDJnew-main/src/main/java/com/personnel/repository/CostBudgetRepository.java`
- Create: `testDJnew-main/src/main/java/com/personnel/dto/WhitelistDTOs.java`
- Create: `testDJnew-main/src/main/java/com/personnel/dto/CostBudgetDTOs.java`
- Create: `testDJnew-main/src/main/java/com/personnel/service/WhitelistService.java`
- Create: `testDJnew-main/src/main/java/com/personnel/service/WhitelistServiceImpl.java`
- Create: `testDJnew-main/src/main/java/com/personnel/service/CostBudgetService.java`
- Create: `testDJnew-main/src/main/java/com/personnel/service/CostBudgetServiceImpl.java`
- Test: `testDJnew-main/src/test/java/com/personnel/service/WhitelistServiceTest.java`
- Test: `testDJnew-main/src/test/java/com/personnel/service/CostBudgetServiceTest.java`

**Interfaces:**
- Consumes: `EmployeeRepository`, `CostBudgetRepository`
- Produces: Whitelist CRUD, CostBudget CRUD

- [ ] **Step 1: Create `ImportWhitelist.java` entity**

```java
package com.personnel.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_whitelist")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportWhitelist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "allowed_action", nullable = false, length = 50)
    private String allowedAction;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 2: Create `PermissionWhitelist.java` entity**

```java
package com.personnel.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "permission_whitelist")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionWhitelist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(nullable = false, length = 50)
    private String permission;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 3: Create `CostBudget.java` entity**

```java
package com.personnel.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cost_budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostBudget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "cost_type", nullable = false, length = 50)
    private String costType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 4: Create repository interfaces**

```java
// ImportWhitelistRepository.java
package com.personnel.repository;

import com.personnel.entity.ImportWhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ImportWhitelistRepository extends JpaRepository<ImportWhitelist, Long> {
    Optional<ImportWhitelist> findByDepartmentAndAllowedAction(String department, String allowedAction);
    boolean existsByDepartmentAndAllowedAction(String department, String allowedAction);
}

// PermissionWhitelistRepository.java
package com.personnel.repository;

import com.personnel.entity.PermissionWhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PermissionWhitelistRepository extends JpaRepository<PermissionWhitelist, Long> {
    Optional<PermissionWhitelist> findByUserIdAndPermission(String userId, String permission);
    boolean existsByUserIdAndPermission(String userId, String permission);
}

// CostBudgetRepository.java
package com.personnel.repository;

import com.personnel.entity.CostBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CostBudgetRepository extends JpaRepository<CostBudget, Long> {
    List<CostBudget> findByEmployeeIdAndYear(Long employeeId, Integer year);
    List<CostBudget> findByEmployeeId(Long employeeId);
}
```

- [ ] **Step 5: Create DTOs**

```java
// WhitelistDTOs.java
package com.personnel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
public class WhitelistEntryCreateRequest {
    @NotBlank(message = "部门不能为空")
    private String department;
    @NotBlank(message = "操作类型不能为空")
    private String allowedAction;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhitelistEntryResponse {
    private Long id;
    private String department;
    private String allowedAction;
    private String createdBy;
    private LocalDateTime createdAt;
}

@Data
public class PermissionWhitelistEntryCreateRequest {
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    @NotBlank(message = "权限不能为空")
    private String permission;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionWhitelistEntryResponse {
    private Long id;
    private String userId;
    private String permission;
    private String createdBy;
    private LocalDateTime createdAt;
}

// CostBudgetDTOs.java
package com.personnel.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CostBudgetCreateRequest {
    @NotBlank(message = "成本类型不能为空")
    private String costType;
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;
    private String description;
    @NotNull(message = "年份不能为空")
    private Integer year;
}

@Data
public class CostBudgetUpdateRequest {
    @NotBlank(message = "成本类型不能为空")
    private String costType;
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;
    private String description;
    @NotNull(message = "年份不能为空")
    private Integer year;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostBudgetResponse {
    private Long id;
    private Long employeeId;
    private String costType;
    private BigDecimal amount;
    private String description;
    private Integer year;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 6: Create service interfaces and implementations**

```java
// WhitelistService.java
package com.personnel.service;

import com.personnel.dto.*;
import java.util.List;

public interface WhitelistService {
    List<WhitelistEntryResponse> listImportWhitelist();
    WhitelistEntryResponse createImportWhitelist(WhitelistEntryCreateRequest request);
    void deleteImportWhitelist(Long id);
    List<PermissionWhitelistEntryResponse> listPermissionWhitelist();
    PermissionWhitelistEntryResponse createPermissionWhitelist(PermissionWhitelistEntryCreateRequest request);
    void deletePermissionWhitelist(Long id);
    boolean isDepartmentAllowedForImport(String department);
    boolean hasPermission(String userId, String permission);
}

// WhitelistServiceImpl.java
package com.personnel.service;

import com.personnel.dto.*;
import com.personnel.entity.ImportWhitelist;
import com.personnel.entity.PermissionWhitelist;
import com.personnel.repository.ImportWhitelistRepository;
import com.personnel.repository.PermissionWhitelistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WhitelistServiceImpl implements WhitelistService {

    private final ImportWhitelistRepository importWhitelistRepository;
    private final PermissionWhitelistRepository permissionWhitelistRepository;

    @Override
    public List<WhitelistEntryResponse> listImportWhitelist() {
        return importWhitelistRepository.findAll().stream()
                .map(this::toImportResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WhitelistEntryResponse createImportWhitelist(WhitelistEntryCreateRequest request) {
        if (importWhitelistRepository.existsByDepartmentAndAllowedAction(
                request.getDepartment(), request.getAllowedAction())) {
            throw new RuntimeException("该部门的导入白名单已存在");
        }
        ImportWhitelist entity = ImportWhitelist.builder()
                .department(request.getDepartment())
                .allowedAction(request.getAllowedAction())
                .createdBy("system")
                .build();
        entity = importWhitelistRepository.save(entity);
        return toImportResponse(entity);
    }

    @Override
    @Transactional
    public void deleteImportWhitelist(Long id) {
        if (!importWhitelistRepository.existsById(id)) {
            throw new RuntimeException("白名单记录不存在");
        }
        importWhitelistRepository.deleteById(id);
    }

    @Override
    public List<PermissionWhitelistEntryResponse> listPermissionWhitelist() {
        return permissionWhitelistRepository.findAll().stream()
                .map(this::toPermissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PermissionWhitelistEntryResponse createPermissionWhitelist(PermissionWhitelistEntryCreateRequest request) {
        if (permissionWhitelistRepository.existsByUserIdAndPermission(
                request.getUserId(), request.getPermission())) {
            throw new RuntimeException("该用户的权限已存在");
        }
        PermissionWhitelist entity = PermissionWhitelist.builder()
                .userId(request.getUserId())
                .permission(request.getPermission())
                .createdBy("system")
                .build();
        entity = permissionWhitelistRepository.save(entity);
        return toPermissionResponse(entity);
    }

    @Override
    @Transactional
    public void deletePermissionWhitelist(Long id) {
        if (!permissionWhitelistRepository.existsById(id)) {
            throw new RuntimeException("权限记录不存在");
        }
        permissionWhitelistRepository.deleteById(id);
    }

    @Override
    public boolean isDepartmentAllowedForImport(String department) {
        return importWhitelistRepository.existsByDepartmentAndAllowedAction(department, "IMPORT");
    }

    @Override
    public boolean hasPermission(String userId, String permission) {
        return permissionWhitelistRepository.existsByUserIdAndPermission(userId, permission);
    }

    private WhitelistEntryResponse toImportResponse(ImportWhitelist entity) {
        return WhitelistEntryResponse.builder()
                .id(entity.getId())
                .department(entity.getDepartment())
                .allowedAction(entity.getAllowedAction())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private PermissionWhitelistEntryResponse toPermissionResponse(PermissionWhitelist entity) {
        return PermissionWhitelistEntryResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .permission(entity.getPermission())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

// CostBudgetService.java
package com.personnel.service;

import com.personnel.dto.*;
import java.util.List;

public interface CostBudgetService {
    List<CostBudgetResponse> listCostBudgets(Long employeeId, Integer year);
    CostBudgetResponse createCostBudget(Long employeeId, CostBudgetCreateRequest request);
    CostBudgetResponse updateCostBudget(Long employeeId, Long costId, CostBudgetUpdateRequest request);
    void deleteCostBudget(Long employeeId, Long costId);
}

// CostBudgetServiceImpl.java
package com.personnel.service;

import com.personnel.dto.*;
import com.personnel.entity.CostBudget;
import com.personnel.repository.CostBudgetRepository;
import com.personnel.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CostBudgetServiceImpl implements CostBudgetService {

    private final CostBudgetRepository costBudgetRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<CostBudgetResponse> listCostBudgets(Long employeeId, Integer year) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new RuntimeException("员工不存在: " + employeeId);
        }
        List<CostBudget> budgets;
        if (year != null) {
            budgets = costBudgetRepository.findByEmployeeIdAndYear(employeeId, year);
        } else {
            budgets = costBudgetRepository.findByEmployeeId(employeeId);
        }
        return budgets.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CostBudgetResponse createCostBudget(Long employeeId, CostBudgetCreateRequest request) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new RuntimeException("员工不存在: " + employeeId);
        }
        CostBudget budget = CostBudget.builder()
                .employeeId(employeeId)
                .costType(request.getCostType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .year(request.getYear())
                .build();
        budget = costBudgetRepository.save(budget);
        return toResponse(budget);
    }

    @Override
    @Transactional
    public CostBudgetResponse updateCostBudget(Long employeeId, Long costId, CostBudgetUpdateRequest request) {
        CostBudget budget = costBudgetRepository.findById(costId)
                .orElseThrow(() -> new RuntimeException("成本预算记录不存在: " + costId));
        if (!budget.getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("成本预算不属于该员工");
        }
        budget.setCostType(request.getCostType());
        budget.setAmount(request.getAmount());
        budget.setDescription(request.getDescription());
        budget.setYear(request.getYear());
        budget = costBudgetRepository.save(budget);
        return toResponse(budget);
    }

    @Override
    @Transactional
    public void deleteCostBudget(Long employeeId, Long costId) {
        CostBudget budget = costBudgetRepository.findById(costId)
                .orElseThrow(() -> new RuntimeException("成本预算记录不存在: " + costId));
        if (!budget.getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("成本预算不属于该员工");
        }
        costBudgetRepository.deleteById(budget);
    }

    private CostBudgetResponse toResponse(CostBudget budget) {
        return CostBudgetResponse.builder()
                .id(budget.getId())
                .employeeId(budget.getEmployeeId())
                .costType(budget.getCostType())
                .amount(budget.getAmount())
                .description(budget.getDescription())
                .year(budget.getYear())
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }
}
```

- [ ] **Step 7: Run all tests**

Run: `cd /workspace/worktree/testDJnew-main && mvn test -q`
Expected: All tests PASS

---

## Task 6: [testDJnew-main] Backend — REST Controllers

**Files:**
- Create: `testDJnew-main/src/main/java/com/personnel/controller/EmployeeController.java`
- Create: `testDJnew-main/src/main/java/com/personnel/controller/WhitelistController.java`
- Create: `testDJnew-main/src/main/java/com/personnel/controller/CostBudgetController.java`
- Create: `testDJnew-main/src/main/java/com/personnel/exception/GlobalExceptionHandler.java`
- Test: `testDJnew-main/src/test/java/com/personnel/controller/EmployeeControllerTest.java`

**Interfaces:**
- Consumes: `EmployeeService`, `WhitelistService`, `CostBudgetService`
- Produces: REST endpoints per API contract

- [ ] **Step 1: Create `EmployeeController.java`**

```java
package com.personnel.controller;

import com.personnel.dto.*;
import com.personnel.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<PageResponse<EmployeeResponse>> listEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(employeeService.listEmployees(page, size, search, department));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id, @Valid @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResult> importEmployees(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(employeeService.importEmployees(file));
    }
}
```

- [ ] **Step 2: Create `WhitelistController.java`**

```java
package com.personnel.controller;

import com.personnel.dto.*;
import com.personnel.service.WhitelistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/whitelist")
@RequiredArgsConstructor
public class WhitelistController {

    private final WhitelistService whitelistService;

    @GetMapping("/import")
    public ResponseEntity<List<WhitelistEntryResponse>> listImportWhitelist() {
        return ResponseEntity.ok(whitelistService.listImportWhitelist());
    }

    @PostMapping("/import")
    public ResponseEntity<WhitelistEntryResponse> createImportWhitelist(
            @Valid @RequestBody WhitelistEntryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(whitelistService.createImportWhitelist(request));
    }

    @DeleteMapping("/import/{id}")
    public ResponseEntity<Void> deleteImportWhitelist(@PathVariable Long id) {
        whitelistService.deleteImportWhitelist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/permission")
    public ResponseEntity<List<PermissionWhitelistEntryResponse>> listPermissionWhitelist() {
        return ResponseEntity.ok(whitelistService.listPermissionWhitelist());
    }

    @PostMapping("/permission")
    public ResponseEntity<PermissionWhitelistEntryResponse> createPermissionWhitelist(
            @Valid @RequestBody PermissionWhitelistEntryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(whitelistService.createPermissionWhitelist(request));
    }

    @DeleteMapping("/permission/{id}")
    public ResponseEntity<Void> deletePermissionWhitelist(@PathVariable Long id) {
        whitelistService.deletePermissionWhitelist(id);
        return ResponseEntity.noContent().build();
    }
}
```

- [ ] **Step 3: Create `CostBudgetController.java`**

```java
package com.personnel.controller;

import com.personnel.dto.*;
import com.personnel.service.CostBudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees/{employeeId}/costs")
@RequiredArgsConstructor
public class CostBudgetController {

    private final CostBudgetService costBudgetService;

    @GetMapping
    public ResponseEntity<List<CostBudgetResponse>> listCostBudgets(
            @PathVariable Long employeeId,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(costBudgetService.listCostBudgets(employeeId, year));
    }

    @PostMapping
    public ResponseEntity<CostBudgetResponse> createCostBudget(
            @PathVariable Long employeeId,
            @Valid @RequestBody CostBudgetCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(costBudgetService.createCostBudget(employeeId, request));
    }

    @PutMapping("/{costId}")
    public ResponseEntity<CostBudgetResponse> updateCostBudget(
            @PathVariable Long employeeId,
            @PathVariable Long costId,
            @Valid @RequestBody CostBudgetUpdateRequest request) {
        return ResponseEntity.ok(costBudgetService.updateCostBudget(employeeId, costId, request));
    }

    @DeleteMapping("/{costId}")
    public ResponseEntity<Void> deleteCostBudget(
            @PathVariable Long employeeId,
            @PathVariable Long costId) {
        costBudgetService.deleteCostBudget(employeeId, costId);
        return ResponseEntity.noContent().build();
    }
}
```

- [ ] **Step 4: Create `GlobalExceptionHandler.java`**

```java
package com.personnel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> body = new HashMap<>();
        body.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSizeException() {
        Map<String, String> body = new HashMap<>();
        body.put("error", "文件大小超出限制（最大10MB）");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body);
    }
}
```

- [ ] **Step 5: Create `EmployeeControllerTest.java`**

```java
package com.personnel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personnel.dto.EmployeeCreateRequest;
import com.personnel.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void shouldCreateAndRetrieveEmployee() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setName("测试员工");
        request.setEmployeeNo("T001");
        request.setDepartment("技术部");
        request.setPosition("工程师");
        request.setHireDate(LocalDate.of(2023, 1, 1));

        // Create
        String json = objectMapper.writeValueAsString(request);
        String createdJson = mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("测试员工"))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdJson).get("id").asLong();

        // Retrieve
        mockMvc.perform(get("/api/v1/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("测试员工"));

        // List
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldImportCsv() throws Exception {
        String csv = "姓名,工号,部门,职位,入职日期\n" +
                     "导入员工,IMP001,技术部,工程师,2023-01-15\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.csv", "text/csv", csv.getBytes());

        mockMvc.perform(multipart("/api/v1/employees/import")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successRows").value(1));
    }
}
```

- [ ] **Step 6: Run all tests**

Run: `cd /workspace/worktree/testDJnew-main && mvn test -q`
Expected: All tests PASS

---

## Task 7: [testDj-main] Frontend — Project Scaffolding

**Files:**
- Create: `testDj-main/package.json`
- Create: `testDj-main/vite.config.ts`
- Create: `testDj-main/tsconfig.json`
- Create: `testDj-main/tsconfig.node.json`
- Create: `testDj-main/index.html`
- Create: `testDj-main/src/main.ts`
- Create: `testDj-main/src/App.vue`
- Create: `testDj-main/src/env.d.ts`
- Create: `testDj-main/src/api/http.ts`
- Create: `testDj-main/src/router/index.ts`

**Interfaces:**
- Consumes: (nothing — this is the frontend foundation)
- Produces: Vue 3 + Vite + TypeScript + Element Plus project skeleton

- [ ] **Step 1: Create `package.json`**

```json
{
  "name": "personnel-dashboard",
  "private": true,
  "version": "0.0.1",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vue-tsc && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.3.0",
    "element-plus": "^2.7.0",
    "axios": "^1.7.0",
    "xlsx": "^0.18.5",
    "@element-plus/icons-vue": "^2.3.1"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "typescript": "^5.4.0",
    "vite": "^5.2.0",
    "vue-tsc": "^2.0.0",
    "@types/node": "^20.12.0"
  }
}
```

- [ ] **Step 2: Create `vite.config.ts`**

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: Create `tsconfig.json`**

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": false,
    "noUnusedParameters": false,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.tsx", "src/**/*.vue"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

- [ ] **Step 4: Create `tsconfig.node.json`**

```json
{
  "compilerOptions": {
    "composite": true,
    "skipLibCheck": true,
    "module": "ESNext",
    "moduleResolution": "bundler",
    "allowSyntheticDefaultImports": true
  },
  "include": ["vite.config.ts"]
}
```

- [ ] **Step 5: Create `index.html`**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <link rel="icon" type="image/svg+xml" href="/vite.svg" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>人员看板</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.ts"></script>
</body>
</html>
```

- [ ] **Step 6: Create `src/main.ts`**

```typescript
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// Register all Element Plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.mount('#app')
```

- [ ] **Step 7: Create `src/App.vue`**

```vue
<template>
  <div id="app-container">
    <el-container>
      <el-header class="app-header">
        <div class="header-title">
          <el-icon :size="24"><UserFilled /></el-icon>
          <span>人员看板管理系统</span>
        </div>
      </el-header>
      <el-container>
        <el-aside width="220px" class="app-aside">
          <el-menu
            :default-active="route.path"
            router
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
          >
            <el-menu-item index="/employees">
              <el-icon><User /></el-icon>
              <span>员工管理</span>
            </el-menu-item>
            <el-menu-item index="/import">
              <el-icon><Upload /></el-icon>
              <span>批量导入</span>
            </el-menu-item>
            <el-menu-item index="/whitelist">
              <el-icon><Lock /></el-icon>
              <span>白名单管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<style>
html, body, #app, #app-container {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
.app-header {
  background-color: #304156;
  color: #fff;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}
.header-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
}
.app-aside {
  background-color: #304156;
  min-height: calc(100vh - 60px);
}
.app-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
```

- [ ] **Step 8: Create `src/env.d.ts`**

```typescript
/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}
```

- [ ] **Step 9: Create `src/api/http.ts`**

```typescript
import axios from 'axios'
import { ElMessage } from 'element-plus'

const http = axios.create({
  baseURL: '/api/v1',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error.response?.data?.error || error.message || '请求失败'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default http
```

- [ ] **Step 10: Create `src/router/index.ts`**

```typescript
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/employees'
    },
    {
      path: '/employees',
      name: 'Employees',
      component: () => import('@/views/employees/EmployeeList.vue'),
      meta: { title: '员工管理' }
    },
    {
      path: '/employees/new',
      name: 'EmployeeCreate',
      component: () => import('@/views/employees/EmployeeForm.vue'),
      meta: { title: '新增员工' }
    },
    {
      path: '/employees/:id/edit',
      name: 'EmployeeEdit',
      component: () => import('@/views/employees/EmployeeForm.vue'),
      meta: { title: '编辑员工' }
    },
    {
      path: '/employees/:id',
      name: 'EmployeeDetail',
      component: () => import('@/views/employees/EmployeeDetail.vue'),
      meta: { title: '员工详情' }
    },
    {
      path: '/import',
      name: 'Import',
      component: () => import('@/views/import/ImportView.vue'),
      meta: { title: '批量导入' }
    },
    {
      path: '/whitelist',
      name: 'Whitelist',
      component: () => import('@/views/whitelist/WhitelistView.vue'),
      meta: { title: '白名单管理' }
    }
  ]
})

export default router
```

- [ ] **Step 11: Create directory structure and verify**

Run: `mkdir -p /workspace/worktree/testDj-main/src/{views/{employees,import,whitelist},api,router,components,types}`
Run: `cd /workspace/worktree/testDj-main && npm install`
Expected: `node_modules` created, no errors

---

## Task 8: [testDj-main] Frontend — Employee List & CRUD Views

**Files:**
- Create: `testDj-main/src/types/employee.ts`
- Create: `testDj-main/src/api/employee.ts`
- Create: `testDj-main/src/views/employees/EmployeeList.vue`
- Create: `testDj-main/src/views/employees/EmployeeForm.vue`
- Create: `testDj-main/src/views/employees/EmployeeDetail.vue`

**Interfaces:**
- Consumes: API contract from Tasks 1-6 (backend endpoints)
- Produces: Employee CRUD UI pages

- [ ] **Step 1: Create `src/types/employee.ts`**

```typescript
export interface Employee {
  id: number
  name: string
  employeeNo: string
  department: string
  position: string
  phone: string
  email: string
  hireDate: string
  salary: number | null
  bankAccount: string
  education: string
  skills: string
  contractEndDate: string
  address: string
  emergencyContact: string
  emergencyPhone: string
  createdAt: string
  updatedAt: string
}

export interface EmployeeCreateRequest {
  name: string
  employeeNo: string
  department: string
  position: string
  phone: string
  email: string
  hireDate: string
  salary?: number
  bankAccount?: string
  education?: string
  skills?: string
  contractEndDate?: string
  address?: string
  emergencyContact?: string
  emergencyPhone?: string
}

export type EmployeeUpdateRequest = EmployeeCreateRequest

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

export interface ImportResult {
  totalRows: number
  successRows: number
  failedRows: number
  errors: ImportError[]
}

export interface ImportError {
  row: number
  column: string
  message: string
}
```

- [ ] **Step 2: Create `src/api/employee.ts`**

```typescript
import http from './http'
import type { Employee, EmployeeCreateRequest, EmployeeUpdateRequest, PageResponse, ImportResult } from '@/types/employee'

export function listEmployees(params: {
  page?: number
  size?: number
  search?: string
  department?: string
}) {
  return http.get<PageResponse<Employee>>('/employees', { params })
}

export function getEmployee(id: number) {
  return http.get<Employee>(`/employees/${id}`)
}

export function createEmployee(data: EmployeeCreateRequest) {
  return http.post<Employee>('/employees', data)
}

export function updateEmployee(id: number, data: EmployeeUpdateRequest) {
  return http.put<Employee>(`/employees/${id}`, data)
}

export function deleteEmployee(id: number) {
  return http.delete(`/employees/${id}`)
}

export function importEmployees(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return http.post<ImportResult>('/employees/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
```

- [ ] **Step 3: Create `EmployeeList.vue`**

```vue
<template>
  <div class="employee-list-container">
    <div class="page-header">
      <h2>员工管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="$router.push('/employees/new')">
          <el-icon><Plus /></el-icon>新增员工
        </el-button>
        <el-button @click="$router.push('/import')">
          <el-icon><Upload /></el-icon>批量导入
        </el-button>
      </div>
    </div>

    <!-- Search -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.search"
            placeholder="姓名/工号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="部门">
          <el-input
            v-model="searchForm.department"
            placeholder="部门名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table -->
    <el-card>
      <el-table :data="employeeList" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="employeeNo" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="department" label="部门" width="150" />
        <el-table-column prop="position" label="职位" width="150" />
        <el-table-column prop="phone" label="联系方式" width="140" />
        <el-table-column prop="hireDate" label="入职日期" width="120" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row.id)">详情</el-button>
            <el-button link type="primary" size="small" @click="editEmployee(row.id)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalElements"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="fetchData"
          @size-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listEmployees, deleteEmployee } from '@/api/employee'
import type { Employee } from '@/types/employee'

const router = useRouter()

const employeeList = ref<Employee[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const totalElements = ref(0)
const searchForm = ref({ search: '', department: '' })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listEmployees({
      page: currentPage.value - 1,
      size: pageSize.value,
      search: searchForm.value.search,
      department: searchForm.value.department
    })
    employeeList.value = res.data.content
    totalElements.value = res.data.totalElements
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const resetSearch = () => {
  searchForm.value = { search: '', department: '' }
  currentPage.value = 1
  fetchData()
}

const viewDetail = (id: number) => {
  router.push(`/employees/${id}`)
}

const editEmployee = (id: number) => {
  router.push(`/employees/${id}/edit`)
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认删除该员工？此操作不可恢复。', '确认删除', {
      type: 'warning'
    })
    await deleteEmployee(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // cancelled
  }
}

onMounted(fetchData)
</script>

<style scoped>
.employee-list-container {
  max-width: 1200px;
  margin: 0 auto;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.header-actions {
  display: flex;
  gap: 8px;
}
.search-card {
  margin-bottom: 16px;
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
```

- [ ] **Step 4: Create `EmployeeForm.vue`**

```vue
<template>
  <div class="employee-form-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑员工' : '新增员工' }}</h2>
    </div>

    <el-card>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="max-width: 800px"
      >
        <el-divider content-position="left">基础信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工号" prop="employeeNo">
              <el-input v-model="form.employeeNo" placeholder="请输入工号" :disabled="isEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-input v-model="form.department" placeholder="请输入部门" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="form.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系方式" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入职日期" prop="hireDate">
              <el-date-picker v-model="form.hireDate" type="date" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">财务信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="薪资" prop="salary">
              <el-input-number v-model="form.salary" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="银行账号" prop="bankAccount">
              <el-input v-model="form.bankAccount" placeholder="请输入银行账号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">人事信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教育背景" prop="education">
              <el-input v-model="form.education" placeholder="如：本科/硕士" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="技能证书" prop="skills">
              <el-input v-model="form.skills" placeholder="技能/证书" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同到期日" prop="contractEndDate">
              <el-date-picker v-model="form.contractEndDate" type="date" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">个人信息</el-divider>
        <el-form-item label="家庭住址" prop="address">
          <el-input v-model="form.address" type="textarea" rows="2" placeholder="请输入家庭住址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系电话" prop="emergencyPhone">
              <el-input v-model="form.emergencyPhone" placeholder="联系人电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '创建员工' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createEmployee, getEmployee, updateEmployee } from '@/api/employee'
import type { EmployeeCreateRequest } from '@/types/employee'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const formRef = ref()
const submitting = ref(false)

const form = ref<EmployeeCreateRequest>({
  name: '',
  employeeNo: '',
  department: '',
  position: '',
  phone: '',
  email: '',
  hireDate: '',
  salary: undefined,
  bankAccount: '',
  education: '',
  skills: '',
  contractEndDate: '',
  address: '',
  emergencyContact: '',
  emergencyPhone: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  employeeNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  department: [{ required: true, message: '请输入部门', trigger: 'blur' }],
  hireDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }]
}

const formatDate = (date: Date | string): string => {
  if (!date) return ''
  if (typeof date === 'string') return date
  return date.toISOString().split('T')[0]
}

const loadEmployee = async () => {
  const id = Number(route.params.id)
  if (!id) return
  try {
    const res = await getEmployee(id)
    const emp = res.data
    form.value = {
      name: emp.name,
      employeeNo: emp.employeeNo,
      department: emp.department,
      position: emp.position || '',
      phone: emp.phone || '',
      email: emp.email || '',
      hireDate: emp.hireDate,
      salary: emp.salary || undefined,
      bankAccount: emp.bankAccount || '',
      education: emp.education || '',
      skills: emp.skills || '',
      contractEndDate: emp.contractEndDate || '',
      address: emp.address || '',
      emergencyContact: emp.emergencyContact || '',
      emergencyPhone: emp.emergencyPhone || ''
    }
  } catch (e) {
    ElMessage.error('加载员工信息失败')
    router.push('/employees')
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload = {
      ...form.value,
      hireDate: formatDate(form.value.hireDate),
      contractEndDate: form.value.contractEndDate ? formatDate(form.value.contractEndDate) : undefined
    }

    if (isEdit.value) {
      await updateEmployee(Number(route.params.id), payload)
      ElMessage.success('修改成功')
    } else {
      await createEmployee(payload)
      ElMessage.success('创建成功')
    }
    router.push('/employees')
  } catch (e) {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (isEdit.value) {
    loadEmployee()
  }
})
</script>

<style scoped>
.employee-form-container {
  max-width: 900px;
  margin: 0 auto;
}
.page-header {
  margin-bottom: 16px;
}
</style>
```

- [ ] **Step 5: Create `EmployeeDetail.vue`**

```vue
<template>
  <div class="employee-detail-container">
    <div class="page-header">
      <h2>员工详情</h2>
      <div class="header-actions">
        <el-button type="primary" @click="router.push(`/employees/${employee.id}/edit`)">编辑</el-button>
        <el-button @click="router.push('/employees')">返回列表</el-button>
      </div>
    </div>

    <el-card v-if="employee" class="detail-card">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名" :span="1">{{ employee.name }}</el-descriptions-item>
        <el-descriptions-item label="工号" :span="1">{{ employee.employeeNo }}</el-descriptions-item>
        <el-descriptions-item label="部门" :span="1">{{ employee.department }}</el-descriptions-item>
        <el-descriptions-item label="职位" :span="1">{{ employee.position }}</el-descriptions-item>
        <el-descriptions-item label="联系方式" :span="1">{{ employee.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱" :span="1">{{ employee.email }}</el-descriptions-item>
        <el-descriptions-item label="入职日期" :span="1">{{ employee.hireDate }}</el-descriptions-item>
        <el-descriptions-item label="薪资" :span="1">{{ employee.salary ? '¥' + employee.salary : '-' }}</el-descriptions-item>
        <el-descriptions-item label="银行账号" :span="1">{{ employee.bankAccount || '-' }}</el-descriptions-item>
        <el-descriptions-item label="教育背景" :span="1">{{ employee.education || '-' }}</el-descriptions-item>
        <el-descriptions-item label="技能证书" :span="2">{{ employee.skills || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同到期日" :span="1">{{ employee.contractEndDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="家庭住址" :span="2">{{ employee.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人" :span="1">{{ employee.emergencyContact || '-' }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系电话" :span="1">{{ employee.emergencyPhone || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Cost Budget Section -->
    <el-card class="cost-card">
      <template #header>
        <div class="card-header">
          <span>成本预算</span>
          <el-button size="small" type="primary" @click="showAddCostDialog = true">添加成本</el-button>
        </div>
      </template>

      <el-table :data="costBudgets" stripe>
        <el-table-column prop="costType" label="成本类型" width="150">
          <template #default="{ row }">
            <el-tag :type="costTypeTag(row.costType)">{{ costTypeLabel(row.costType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="150">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="year" label="年度" width="100" />
        <el-table-column prop="description" label="说明" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click="handleDeleteCost(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add Cost Dialog -->
    <el-dialog v-model="showAddCostDialog" title="添加成本预算" width="500px">
      <el-form :model="costForm" label-width="100px">
        <el-form-item label="成本类型" required>
          <el-select v-model="costForm.costType" style="width: 100%">
            <el-option label="薪资" value="SALARY" />
            <el-option label="培训" value="TRAINING" />
            <el-option label="差旅" value="TRAVEL" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" required>
          <el-input-number v-model="costForm.amount" :min="0.01" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="年度" required>
          <el-input-number v-model="costForm.year" :min="2020" :max="2030" style="width: 100%" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="costForm.description" type="textarea" rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddCostDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddCost" :loading="costSubmitting">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getEmployee } from '@/api/employee'
import { listCostBudgets, createCostBudget, deleteCostBudget } from '@/api/cost'
import type { Employee, CostBudget } from '@/types/employee'

const route = useRoute()
const router = useRouter()
const employee = ref<Employee | null>(null)
const costBudgets = ref<CostBudget[]>([])
const showAddCostDialog = ref(false)
const costSubmitting = ref(false)
const costForm = ref({ costType: 'SALARY', amount: 0, year: new Date().getFullYear(), description: '' })

const costTypeLabel = (type: string) => {
  const map: Record<string, string> = { SALARY: '薪资', TRAINING: '培训', TRAVEL: '差旅', OTHER: '其他' }
  return map[type] || type
}

const costTypeTag = (type: string) => {
  const map: Record<string, string> = { SALARY: 'success', TRAINING: 'warning', TRAVEL: 'info', OTHER: '' }
  return map[type] || ''
}

const loadCostBudgets = async () => {
  const id = Number(route.params.id)
  if (!id) return
  try {
    const res = await listCostBudgets(id)
    costBudgets.value = res.data
  } catch {
    // ignore
  }
}

const handleAddCost = async () => {
  costSubmitting.value = true
  try {
    await createCostBudget(Number(route.params.id), costForm.value)
    ElMessage.success('添加成功')
    showAddCostDialog.value = false
    costForm.value = { costType: 'SALARY', amount: 0, year: new Date().getFullYear(), description: '' }
    loadCostBudgets()
  } finally {
    costSubmitting.value = false
  }
}

const handleDeleteCost = async (costId: number) => {
  try {
    await deleteCostBudget(Number(route.params.id), costId)
    ElMessage.success('删除成功')
    loadCostBudgets()
  } catch {
    // ignore
  }
}

onMounted(async () => {
  const id = Number(route.params.id)
  if (id) {
    try {
      const res = await getEmployee(id)
      employee.value = res.data
    } catch {
      router.push('/employees')
    }
    loadCostBudgets()
  }
})
</script>

<style scoped>
.employee-detail-container {
  max-width: 1000px;
  margin: 0 auto;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.header-actions {
  display: flex;
  gap: 8px;
}
.detail-card {
  margin-bottom: 20px;
}
.cost-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
```

- [ ] **Step 6: Create cost API file**

```typescript
// src/api/cost.ts
import http from './http'
import type { CostBudget } from '@/types/employee'

export function listCostBudgets(employeeId: number, year?: number) {
  return http.get<CostBudget[]>(`/employees/${employeeId}/costs`, { params: { year } })
}

export function createCostBudget(employeeId: number, data: { costType: string; amount: number; year: number; description: string }) {
  return http.post<CostBudget>(`/employees/${employeeId}/costs`, data)
}

export function deleteCostBudget(employeeId: number, costId: number) {
  return http.delete(`/employees/${employeeId}/costs/${costId}`)
}
```

---

## Task 9: [testDj-main] Frontend — Import & Whitelist Views

**Files:**
- Create: `testDj-main/src/views/import/ImportView.vue`
- Create: `testDj-main/src/views/whitelist/WhitelistView.vue`
- Create: `testDj-main/src/api/whitelist.ts`

**Interfaces:**
- Consumes: API contract from Tasks 1-6 (import/whitelist endpoints)
- Produces: Import page, Whitelist management page

- [ ] **Step 1: Create `src/api/whitelist.ts`**

```typescript
import http from './http'

export interface WhitelistEntry {
  id: number
  department: string
  allowedAction: string
  createdBy: string
  createdAt: string
}

export interface PermissionWhitelistEntry {
  id: number
  userId: string
  permission: string
  createdBy: string
  createdAt: string
}

export function listImportWhitelist() {
  return http.get<WhitelistEntry[]>('/whitelist/import')
}

export function createImportWhitelist(data: { department: string; allowedAction: string }) {
  return http.post<WhitelistEntry>('/whitelist/import', data)
}

export function deleteImportWhitelist(id: number) {
  return http.delete(`/whitelist/import/${id}`)
}

export function listPermissionWhitelist() {
  return http.get<PermissionWhitelistEntry[]>('/whitelist/permission')
}

export function createPermissionWhitelist(data: { userId: string; permission: string }) {
  return http.post<PermissionWhitelistEntry>('/whitelist/permission', data)
}

export function deletePermissionWhitelist(id: number) {
  return http.delete(`/whitelist/permission/${id}`)
}
```

- [ ] **Step 2: Create `ImportView.vue`**

```vue
<template>
  <div class="import-container">
    <div class="page-header">
      <h2>批量导入员工</h2>
    </div>

    <el-card class="import-card">
      <template #header>
        <span>上传文件</span>
      </template>

      <el-upload
        drag
        action="#"
        :auto-upload="false"
        :on-change="handleFileChange"
        :limit="1"
        accept=".csv,.xlsx,.xls"
      >
        <el-icon class="el-icon--upload" :size="48"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将文件拖拽到此处，或 <em>点击选择文件</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 CSV、Excel (.xlsx/.xls) 格式，单文件不超过 10MB
          </div>
        </template>
      </el-upload>

      <div class="import-actions" v-if="selectedFile">
        <el-button type="primary" @click="handleImport" :loading="importing">
          开始导入
        </el-button>
        <el-button @click="selectedFile = null">取消</el-button>
      </div>
    </el-card>

    <!-- Import Result -->
    <el-card v-if="importResult" class="result-card">
      <template #header>
        <span>导入结果</span>
      </template>
      <el-result
        :icon="importResult.failedRows === 0 ? 'success' : 'warning'"
        :title="`导入完成：成功 ${importResult.successRows} 条，失败 ${importResult.failedRows} 条`"
        :sub-title="`共处理 ${importResult.totalRows} 条数据`"
      >
        <template #extra>
          <el-button type="primary" @click="selectedFile = null; importResult = null">继续导入</el-button>
          <el-button @click="$router.push('/employees')">查看员工列表</el-button>
        </template>
      </el-result>

      <!-- Error Details -->
      <el-table v-if="importResult.errors.length > 0" :data="importResult.errors" stripe style="margin-top: 16px">
        <el-table-column prop="row" label="行号" width="80" />
        <el-table-column prop="column" label="字段" width="120" />
        <el-table-column prop="message" label="错误信息" />
      </el-table>
    </el-card>

    <!-- Download Template -->
    <el-card class="template-card">
      <template #header>
        <span>导入模板</span>
      </template>
      <p>请按照以下格式准备导入文件：</p>
      <el-table :data="templateData" border stripe>
        <el-table-column prop="field" label="字段" />
        <el-table-column prop="required" label="必填" width="80" />
        <el-table-column prop="description" label="说明" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { importEmployees } from '@/api/employee'
import type { ImportResult } from '@/types/employee'

const selectedFile = ref<File | null>(null)
const importing = ref(false)
const importResult = ref<ImportResult | null>(null)

const templateData = [
  { field: '姓名', required: '是', description: '员工姓名' },
  { field: '工号', required: '是', description: '员工唯一工号' },
  { field: '部门', required: '是', description: '所属部门' },
  { field: '职位', required: '否', description: '职位名称' },
  { field: '入职日期', required: '是', description: '格式: yyyy-MM-dd' },
  { field: '电话', required: '否', description: '联系方式' },
  { field: '邮箱', required: '否', description: '电子邮箱' },
  { field: '薪资', required: '否', description: '数字金额' },
  { field: '银行账号', required: '否', description: '银行账号' },
  { field: '教育背景', required: '否', description: '学历信息' },
  { field: '技能证书', required: '否', description: '技能或证书' },
  { field: '合同到期日', required: '否', description: '格式: yyyy-MM-dd' },
  { field: '家庭住址', required: '否', description: '地址信息' },
  { field: '紧急联系人', required: '否', description: '联系人姓名' },
  { field: '紧急联系电话', required: '否', description: '联系人电话' }
]

const handleFileChange = (uploadFile: any) => {
  selectedFile.value = uploadFile.raw
  importResult.value = null
}

const handleImport = async () => {
  if (!selectedFile.value) return
  importing.value = true
  try {
    const res = await importEmployees(selectedFile.value)
    importResult.value = res.data
    selectedFile.value = null
  } catch {
    // handled by interceptor
  } finally {
    importing.value = false
  }
}
</script>

<style scoped>
.import-container {
  max-width: 900px;
  margin: 0 auto;
}
.page-header {
  margin-bottom: 16px;
}
.import-card, .result-card, .template-card {
  margin-bottom: 20px;
}
.import-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
  justify-content: center;
}
</style>
```

- [ ] **Step 3: Create `WhitelistView.vue`**

```vue
<template>
  <div class="whitelist-container">
    <div class="page-header">
      <h2>白名单管理</h2>
    </div>

    <el-tabs v-model="activeTab">
      <!-- Import Whitelist -->
      <el-tab-pane label="导入白名单" name="import">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>导入白名单配置</span>
              <el-button size="small" type="primary" @click="showImportDialog = true">
                <el-icon><Plus /></el-icon>新增
              </el-button>
            </div>
          </template>
          <el-table :data="importWhitelist" stripe>
            <el-table-column prop="department" label="部门" />
            <el-table-column prop="allowedAction" label="允许操作" width="150">
              <template #default="{ row }">
                <el-tag>{{ row.allowedAction === 'IMPORT' ? '导入' : row.allowedAction }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="180" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button link type="danger" size="small" @click="handleDeleteImport(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- Permission Whitelist -->
      <el-tab-pane label="操作权限白名单" name="permission">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>操作权限白名单配置</span>
              <el-button size="small" type="primary" @click="showPermissionDialog = true">
                <el-icon><Plus /></el-icon>新增
              </el-button>
            </div>
          </template>
          <el-table :data="permissionWhitelist" stripe>
            <el-table-column prop="userId" label="用户ID" />
            <el-table-column prop="permission" label="权限" width="200">
              <template #default="{ row }">
                <el-tag :type="permissionTag(row.permission)">{{ permissionLabel(row.permission) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="180" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button link type="danger" size="small" @click="handleDeletePermission(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- Add Import Whitelist Dialog -->
    <el-dialog v-model="showImportDialog" title="新增导入白名单" width="400px">
      <el-form :model="importForm" label-width="100px">
        <el-form-item label="部门" required>
          <el-input v-model="importForm.department" placeholder="部门名称" />
        </el-form-item>
        <el-form-item label="允许操作" required>
          <el-select v-model="importForm.allowedAction" style="width: 100%">
            <el-option label="导入" value="IMPORT" />
            <el-option label="导出" value="EXPORT" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddImport" :loading="importSubmitting">确认</el-button>
      </template>
    </el-dialog>

    <!-- Add Permission Whitelist Dialog -->
    <el-dialog v-model="showPermissionDialog" title="新增操作权限" width="400px">
      <el-form :model="permissionForm" label-width="100px">
        <el-form-item label="用户ID" required>
          <el-input v-model="permissionForm.userId" placeholder="用户标识" />
        </el-form-item>
        <el-form-item label="权限" required>
          <el-select v-model="permissionForm.permission" style="width: 100%">
            <el-option label="查看成本预算" value="VIEW_COST" />
            <el-option label="编辑成本预算" value="EDIT_COST" />
            <el-option label="查看全部" value="VIEW_ALL" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPermissionDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddPermission" :loading="permissionSubmitting">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listImportWhitelist, createImportWhitelist, deleteImportWhitelist,
  listPermissionWhitelist, createPermissionWhitelist, deletePermissionWhitelist
} from '@/api/whitelist'
import type { WhitelistEntry, PermissionWhitelistEntry } from '@/api/whitelist'

const activeTab = ref('import')
const importWhitelist = ref<WhitelistEntry[]>([])
const permissionWhitelist = ref<PermissionWhitelistEntry[]>([])
const showImportDialog = ref(false)
const showPermissionDialog = ref(false)
const importSubmitting = ref(false)
const permissionSubmitting = ref(false)
const importForm = ref({ department: '', allowedAction: 'IMPORT' })
const permissionForm = ref({ userId: '', permission: 'VIEW_COST' })

const permissionLabel = (perm: string) => {
  const map: Record<string, string> = { VIEW_COST: '查看成本预算', EDIT_COST: '编辑成本预算', VIEW_ALL: '查看全部' }
  return map[perm] || perm
}

const permissionTag = (perm: string) => {
  const map: Record<string, string> = { VIEW_COST: 'success', EDIT_COST: 'warning', VIEW_ALL: '' }
  return map[perm] || ''
}

const loadData = async () => {
  try {
    const [importRes, permRes] = await Promise.all([
      listImportWhitelist(),
      listPermissionWhitelist()
    ])
    importWhitelist.value = importRes.data
    permissionWhitelist.value = permRes.data
  } catch {
    // ignore
  }
}

const handleAddImport = async () => {
  importSubmitting.value = true
  try {
    await createImportWhitelist(importForm.value)
    ElMessage.success('添加成功')
    showImportDialog.value = false
    importForm.value = { department: '', allowedAction: 'IMPORT' }
    loadData()
  } finally {
    importSubmitting.value = false
  }
}

const handleDeleteImport = async (id: number) => {
  try {
    await deleteImportWhitelist(id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // ignore
  }
}

const handleAddPermission = async () => {
  permissionSubmitting.value = true
  try {
    await createPermissionWhitelist(permissionForm.value)
    ElMessage.success('添加成功')
    showPermissionDialog.value = false
    permissionForm.value = { userId: '', permission: 'VIEW_COST' }
    loadData()
  } finally {
    permissionSubmitting.value = false
  }
}

const handleDeletePermission = async (id: number) => {
  try {
    await deletePermissionWhitelist(id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // ignore
  }
}

onMounted(loadData)
</script>

<style scoped>
.whitelist-container {
  max-width: 900px;
  margin: 0 auto;
}
.page-header {
  margin-bottom: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
```

---

## Self-Review

### 1. Spec Coverage Check

| Requirement | Task Coverage |
|---|---|
| 员工基本信息 CRUD (增删改查) | Task 2 (entity/repo), Task 3 (service/DTOs), Task 6 (controller), Task 8 (frontend CRUD views) |
| 批量导入 (CSV + Excel) | Task 4 (file import service), Task 9 (ImportView.vue) |
| 成本预算管理 | Task 5 (CostBudget entity/service), Task 6 (controller), Task 8 (EmployeeDetail cost section) |
| 导入白名单 | Task 5 (ImportWhitelist entity/service), Task 6 (controller), Task 9 (WhitelistView.vue) |
| 操作权限白名单 | Task 5 (PermissionWhitelist entity/service), Task 6 (controller), Task 9 (WhitelistView.vue) |
| 跨仓库协同 | Task 1-6 (backend in testDJnew-main), Task 7-9 (frontend in testDj-main) |

### 2. Placeholder Scan

No placeholders, TODOs, or "implement later" patterns found. Every step contains complete code.

### 3. Type Consistency Check

- Backend `EmployeeResponse` fields match frontend `Employee` interface
- All API path/query parameter names match between controller `@RequestParam` and frontend Axios calls
- DTOs use consistent field names across create/update/response
- All `costType` values use same enum-like strings (`SALARY`, `TRAINING`, `TRAVEL`, `OTHER`)

---

## Execution Handoff

**Plan complete and saved to `.agents/20260818-开发一个人员看板，有入口记录员工的基本信/plan.md`.**

Two execution options:

1. **Subagent-Driven (recommended)** — Dispatch a fresh subagent per task, review between tasks, fast iteration

2. **Inline Execution** — Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
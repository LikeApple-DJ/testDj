# SDD Progress Ledger

## Task 1: Database Init
- Status: complete
- Files: src/main/resources/db/V1__init_schema.sql
- Commits: a6606b5..9782abc

## Task 2: Entity & MyBatis-Plus Config
- Status: complete
- Files: Department.java, Employee.java, TransferRecord.java, MybatisPlusConfig.java
- Commits: a6606b5..9782abc

## Task 3: Mapper & Service Scaffold
- Status: complete
- Files: *Mapper, *Service, *ServiceImpl
- Commits: a6606b5..9782abc

## Task 4: Department Tree (get + move + cycle detection)
- Status: complete
- Files: DepartmentController.java, DepartmentTreeDTO.java, DepartmentServiceImpl.java
- Commits: a6606b5..9782abc

## Task 5: Employee Create (uniqueness check)
- Status: complete
- Files: EmployeeController.java, EmployeeDTO.java, EmployeeServiceImpl.java
- Commits: a6606b5..9782abc

## Task 6: Employee Transfer (snapshot + optimistic lock)
- Status: complete
- Files: EmployeeController.java, TransferDTO.java, EmployeeServiceImpl.java
- Commits: a6606b5..9782abc

## Task 7: Employee Resign (logical delete)
- Status: complete
- Files: EmployeeController.java, ResignDTO.java, EmployeeServiceImpl.java
- Commits: a6606b5..9782abc

## Task 8: Pagination + Dept Delete Guard + Global Exception
- Status: complete
- Files: EmployeeController.java, DepartmentController.java, GlobalExceptionHandler.java
- Commits: a6606b5..9782abc

## Task 9: Backend Fixes (path calculation, CORS, optimistic lock 409)
- Status: complete
- Files: DepartmentServiceImpl.java, CorsConfig.java, GlobalExceptionHandler.java
- Commits: d23851a

## Task 10: Frontend Vue3 Project Setup
- Status: complete
- Files: frontend/package.json, frontend/vite.config.js, frontend/index.html, frontend/src/main.js
- Commits: d23851a

## Task 11: Frontend Components (DepartmentTree, EmployeeTable, Forms)
- Status: complete
- Files: frontend/src/components/*.vue, frontend/src/api/*.js
- Commits: d23851a

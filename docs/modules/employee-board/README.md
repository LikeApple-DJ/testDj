# 人员看板（employee-board）模块文档

## 模块职责

统一管理员工基本信息（增删改查/看板汇总）、Excel 批量导入、员工成本预算记录、管理端白名单访问控制。

## 关键类

| 模块 | 关键类 |
|------|--------|
| employee | EmployeeDO / EmployeeDTO / EmployeeCreateReq / EmployeeUpdateReq / EmployeeQueryReq / EmployeeSummaryDTO / EmployeeMapper / EmployeeService(Impl) / EmployeeController |
| import | ImportTaskDO / ImportDetailDO / ImportTaskDTO / ImportTaskMapper / ImportService(Impl) / ImportController |
| budget | BudgetDO / BudgetDTO / BudgetSaveReq / BudgetMapper / BudgetService(Impl) / BudgetController |
| whitelist | WhitelistDO / WhitelistMapper / WhitelistService(Impl) / WhitelistController |
| common | Result / PageResult / BizException / GlobalExceptionHandler / UserContext / AuthInterceptor / WebConfig |

## 依赖关系

- import → employee（落库）、budget（行内预算）、POI（Excel 解析）
- budget → employee（Mapper 校验员工存在）
- employee → budget（详情/汇总预算信息）
- whitelist 被 AuthInterceptor 横向依赖（/api/** 全部管理端接口）
- 无循环依赖

## API 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/employee/create | 新增员工 |
| POST | /api/employee/update | 编辑员工 |
| POST | /api/employee/delete | 删除员工（软删） |
| POST | /api/employee/pageQuery | 员工分页查询 |
| GET | /api/employee/detail | 员工详情（含最近一期预算摘要） |
| GET | /api/employee/summary | 看板汇总统计 |
| GET | /api/import/template | 导入模板下载 |
| POST | /api/import/upload | 批量导入上传 |
| GET | /api/import/taskDetail | 导入批次结果查询 |
| POST | /api/budget/save | 保存成本预算 |
| GET | /api/budget/queryByEmp | 员工预算列表查询 |
| POST | /api/whitelist/add | 白名单新增 |
| POST | /api/whitelist/remove | 白名单移除 |
| POST | /api/whitelist/pageQuery | 白名单分页查询 |

## 访问控制

所有 /api/** 接口经 AuthInterceptor：从请求头 `X-Emp-No` 提取当前用户工号（统一登录态网关透传），白名单校验不通过返回 WL_001。

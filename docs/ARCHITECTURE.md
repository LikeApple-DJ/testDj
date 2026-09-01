# 架构文档

## 技术栈

- Spring Boot 3.2.5（Java 17）
- MyBatis-Plus 3.5.6 + MySQL 8
- Lombok / spring-boot-starter-validation / spring-boot-starter-test

## 分层结构

```
controller  -> controller 层，REST 入口，@Valid 校验，统一 Result<T> 响应
service      -> 业务接口（继承 IService）
service/impl -> 业务实现（继承 ServiceImpl），事务边界，抛 BusinessException
mapper       -> MyBatis-Plus BaseMapper 数据访问
entity       -> 数据对象（@TableName，乐观锁 @Version，逻辑删除 @TableLogic）
dto          -> DTO(入参) / VO(出参)
exception    -> BusinessException + GlobalExceptionHandler 全局兜底
config       -> MybatisPlusConfig（分页 + 乐观锁拦截器）、CorsConfig
```

## 全局约定

- 统一响应：`Result<T>`（code/msg/data）。
- 逻辑删除字段：`is_deleted`（全局配置 logic-delete-field=isDeleted）。
- 乐观锁：`version` 字段 + `@Version`。
- 异常：业务异常统一抛 `BusinessException(code, message)`，由 `GlobalExceptionHandler` 转换。
- 日志：SLF4J 占位符，禁止 System.out / printStackTrace。

## 模块清单

| 模块 | 职责 | 文档 |
|------|------|------|
| department | 部门树形结构管理 | - |
| employee | 员工全生命周期管理（新增/调动/离职/分页/唯一性校验） | - |
| transfer | 员工调动记录快照 | - |
| todo | 待办事项记录（最小闭环：新增） | [todo/README.md](modules/todo/README.md) |

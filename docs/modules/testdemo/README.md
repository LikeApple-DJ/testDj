# testdemo 模块

## 模块职责
提供计算器四则运算服务（加/减/乘/除），包含参数校验与业务异常处理。

## 关键类

| 类 | 类型 | 说明 |
|---|------|------|
| CalculatorService | 接口 | 计算器服务接口 |
| CalculatorServiceImpl | 实现 | 核心计算逻辑实现 |
| CalculatorDO | 数据对象 | 计算记录（含 gmt_create/gmt_modified） |
| CalculatorRequest | 请求 DTO | @NotBlank 校验 |
| CalculatorVO | 视图对象 | 含 DO→VO 转换 |
| BusinessException | 异常 | 业务异常（errorCode + message） |
| CalculatorConstants | 常量 | 操作类型 & 精度常量 |

## 依赖关系
无外部模块依赖。独立单模块。

## API 接口
### CalculatorService#calculate
- **输入**: operation (ADD/SUBTRACT/MULTIPLY/DIVIDE), operandA, operandB
- **输出**: CalculatorVO (operation, operandA, operandB, result)
- **异常**: BusinessException (B0001-B0004)
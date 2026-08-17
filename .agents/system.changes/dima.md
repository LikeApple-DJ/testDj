# 需求澄清与设计方案

## 一、仓库分配

| 仓库 | 角色 | 技术栈 |
|------|------|--------|
| testDj-main | 后端 | Spring Boot 3.x + Apache POI (Excel导出) |
| testDJnew-main | 前端 | Vue 3 + Vite + Axios |

## 二、后端接口设计 (testDj-main)

### 2.1 接口列表

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| HelloWorld | GET | `/api/hello` | 返回欢迎信息 |
| SHA-256 哈希 | POST | `/api/hash/sha256` | 输入字符串，返回 SHA-256 哈希值 |
| 冒泡排序 | POST | `/api/sort/bubble` | 后端随机生成数组并排序，返回排序过程和结果 |
| 导出 | GET | `/api/export?type={tab}` | 导出指定 Tab 的结果为 Excel (.xlsx) |

### 2.2 HelloWorld 接口
- **请求**: `GET /api/hello`
- **响应**:
```json
{
  "message": "Hello World!",
  "timestamp": "2025-01-01T12:00:00"
}
```

### 2.3 SHA-256 哈希接口
- **请求**: `POST /api/hash/sha256`
- **请求体**:
```json
{
  "input": "待加密字符串"
}
```
- **响应**:
```json
{
  "input": "待加密字符串",
  "algorithm": "SHA-256",
  "hash": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
}
```

### 2.4 冒泡排序接口
- **请求**: `POST /api/sort/bubble`
- **请求体** (可选):
```json
{
  "arraySize": 10,
  "min": 1,
  "max": 100
}
```
- **响应**:
```json
{
  "originalArray": [64, 34, 25, 12, 22, 11, 90],
  "sortedArray": [11, 12, 22, 25, 34, 64, 90],
  "steps": [
    {"round": 1, "array": [34, 25, 12, 22, 11, 64, 90]},
    {"round": 2, "array": [25, 12, 22, 11, 34, 64, 90]}
  ],
  "totalRounds": 6,
  "swapCount": 10
}
```

### 2.5 导出接口
- **请求**: `GET /api/export?type=hello|hash|sort`
- **响应**: `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- 文件下载，Excel 包含三列：Tab 名称、数据内容、时间戳

## 三、前端页面设计 (testDJnew-main)

### 3.1 页面结构
```
页面: DemoPage.vue
├── Tab 1: Hello World
│   └─ 显示后端返回的问候消息和时间戳
├── Tab 2: SHA-256 哈希
│   ├─ 输入框 (输入待加密字符串)
│   ├─ 加密按钮
│   └─ 结果显示 (原始字符串、算法名、哈希值)
├── Tab 3: 冒泡排序
│   ├─ 参数配置 (数组大小、最小值、最大值)
│   ├─ 排序按钮
│   └─ 结果显示 (原始数组、排序过程、最终结果)
└── 导出按钮 (全局，导出当前 Tab 数据为 Excel)
```

### 3.2 组件树
```
App.vue
└── DemoPage.vue
    ├── TabsComponent.vue (Tab 切换)
    ├── HelloPanel.vue (Tab1 内容)
    ├── HashPanel.vue   (Tab2 内容)
    ├── SortPanel.vue   (Tab3 内容)
    └── ExportButton.vue (导出按钮)
```

### 3.3 数据流
```
用户操作 → Vue 组件 → Axios 调用 → Spring Boot 后端 → 数据处理 → 响应返回
                                                                     ↓
                                          前端展示结果 ← 更新响应式数据
                                                                     ↓
                                          点击导出 → 调用导出接口 → 下载 Excel
```

## 四、跨仓依赖与对齐点

| 依赖项 | 说明 | 对齐点 |
|--------|------|--------|
| API 路径 | 前后端需统一接口路径 | `/api/hello`, `/api/hash/sha256`, `/api/sort/bubble`, `/api/export` |
| 请求/响应格式 | 前后端需约定 JSON 结构 | 见上方接口设计 |
| 导出文件类型 | 后端生成 Excel，前端接收文件流 | Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` |
| 导出参数 | 前端传入 `type` 参数 | `type=hello|hash|sort` |

## 五、待确认事项
- [ ] 冒泡排序的随机数组默认参数范围（默认大小 10，范围 1-100）
- [ ] 导出 Excel 的列名和格式细节
- [ ] 端口配置（后端默认 8080，前端是否使用代理转发）
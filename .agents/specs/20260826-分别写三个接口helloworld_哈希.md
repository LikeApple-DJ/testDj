# 需求澄清设计文档

## 1. 概述

本文档描述了一个简单的全栈演示项目，包含三个后端接口（helloworld、哈希算法、冒泡排序）、一个前端 React 页面（含三个 Tab）和一个导出功能。

## 2. 技术选型

| 层级 | 技术 | 理由 |
|------|------|------|
| 后端 | Node.js + Express.js | 全栈 JS 统一语言、生态丰富、快速开发 |
| 前端 | React + 原生 CSS | 组件化、状态管理方便、适合 Tab 页面 |
| 构建工具 | Vite | 快速 HMR、零配置启动 |
| 运行环境 | Node.js 18+ | 跨平台、长期支持 |

## 3. 接口设计

### 3.1 GET /api/helloworld

- **功能**: 返回简单的问候消息
- **请求参数**: 无
- **响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "greeting": "Hello World!",
    "timestamp": "2026-08-26 12:00:00"
  }
}
```

### 3.2 POST /api/hash

- **功能**: 对输入字符串进行 SHA-256 哈希计算
- **请求参数**:
```json
{
  "input": "待哈希的字符串"
}
```
- **响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "input": "待哈希的字符串",
    "algorithm": "SHA-256",
    "hash": "e3b0c44298fc1c149afbf4c8996fb924..."
  }
}
```

### 3.3 POST /api/sort

- **功能**: 对输入整数数组进行冒泡排序，返回排序结果及过程统计
- **请求参数**:
```json
{
  "array": [5, 3, 8, 1, 9, 2]
}
```
- **响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "original": [5, 3, 8, 1, 9, 2],
    "sorted": [1, 2, 3, 5, 8, 9],
    "algorithm": "bubble_sort",
    "swaps": 10,
    "comparisons": 15
  }
}
```

### 3.4 GET /api/export

- **功能**: 导出当前服务端内存中保存的最新执行结果，提供 CSV/JSON 格式下载
- **请求参数**: `?format=csv` 或 `?format=json`
- **状态管理说明**: 服务端在内存中维护三个变量，分别记录最近一次 /api/helloworld、/api/hash、/api/sort 的请求结果。每当上述接口被调用时，对应的结果变量会被更新。导出接口读取这些变量组装为导出文件。
- **响应**: 文件下载（Content-Disposition: attachment）
- **导出文件名**: `api_export_YYYYMMDD_HHMMSS.{csv|json}`
- **导出内容示例（JSON 格式）**:
```json
{
  "export_time": "2026-08-26 12:00:00",
  "helloworld": {
    "greeting": "Hello World!",
    "timestamp": "2026-08-26 12:00:00"
  },
  "hash": {
    "input": "hello",
    "algorithm": "SHA-256",
    "hash": "2cf24dba5fb0a30e26e83b2ac5b9e29e..."
  },
  "sort": {
    "original": [5, 3, 8, 1, 9, 2],
    "sorted": [1, 2, 3, 5, 8, 9],
    "swaps": 10,
    "comparisons": 15
  }
}
```

## 4. 前端页面设计

### 4.1 页面布局

```
┌──────────────────────────────────────────────────────┐
│  API 演示工具                                         │
├──────────────────────────────────────────────────────┤
│  [Hello World]  [哈希算法]  [冒泡排序]   [导出 ▼]     │
├──────────────────────────────────────────────────────┤
│                                                       │
│  当前 Tab 的内容区域                                   │
│  - 输入表单（哈希/排序需要输入）                       │
│  - 执行按钮                                           │
│  - 结果展示区域（卡片式布局）                          │
│                                                       │
└──────────────────────────────────────────────────────┘
```

### 4.2 组件结构

```
src/
├── App.jsx              # 主应用，包含 Tab 切换和路由
├── components/
│   ├── TabBar.jsx       # Tab 导航栏组件
│   ├── HelloWorldTab.jsx  # Hello World Tab
│   ├── HashTab.jsx      # 哈希算法 Tab
│   ├── SortTab.jsx      # 冒泡排序 Tab
│   └── ExportButton.jsx # 导出按钮组件
└── App.css              # 全局样式
```

### 4.3 Tab 功能说明

| Tab | 输入 | 展示内容 |
|-----|------|----------|
| Hello World | 无输入，点击即执行 | 服务端返回的问候语和时间戳 |
| 哈希算法 | 文本输入框 | 输入原文、算法名称、哈希结果 |
| 冒泡排序 | 数字输入（逗号分隔） | 原始数组、排序后数组、交换次数、比较次数 |

### 4.4 导出功能

- 点击"导出"按钮，展示格式选择下拉菜单（CSV / JSON）
- 选择格式后触发下载
- 请求导出接口 GET /api/export?format=csv 或 format=json
- 下载后浏览器保存为 `api_export_YYYYMMDD_HHMMSS.{csv|json}`

## 5. 项目文件结构

```
project/
├── package.json              # 项目依赖配置
├── vite.config.js            # Vite 构建配置 + API 代理
├── server/
│   └── index.js              # Express 后端服务（所有接口 + 状态管理）
├── src/
│   ├── main.jsx              # React 入口
│   ├── App.jsx               # 主应用组件
│   ├── App.css               # 全局样式
│   └── components/
│       ├── TabBar.jsx         # Tab 导航栏
│       ├── HelloWorldTab.jsx  # Hello World Tab
│       ├── HashTab.jsx        # 哈希算法 Tab
│       ├── SortTab.jsx        # 冒泡排序 Tab
│       └── ExportButton.jsx   # 导出按钮组件
├── index.html                # HTML 入口
└── README.md                 # 项目说明
```

## 6. 错误处理规范

所有接口统一错误响应格式：
```json
{
  "code": 400,
  "message": "具体的错误描述",
  "data": null
}
```

- 哈希接口：输入为空或非字符串时返回 400 错误
- 排序接口：输入非数字数组或数组为空时返回 400 错误
- 导出接口：不支持的格式参数（非 csv/json）返回 400 错误

## 7. 启动方式

```bash
# 安装依赖
npm install

# 同时启动前端（Vite 开发服务器）和后端（Express）
npm run dev

# 访问 http://localhost:5173
# 后端运行在 http://localhost:3000
# Vite 代理 /api 请求到后端
```

## 8. 依赖清单

**package.json 关键依赖**:
```json
{
  "dependencies": {
    "express": "^4.18.0",
    "cors": "^2.8.5"
  },
  "devDependencies": {
    "vite": "^5.0.0",
    "@vitejs/plugin-react": "^4.0.0",
    "react": "^18.2.0",
    "react-dom": "^18.2.0"
  }
}
```

## 9. 验收标准

1. 三个接口均可通过 curl/Postman 调试，返回正确的 JSON 格式
2. 前端页面三个 Tab 切换正常，结果展示正确
3. 导出按钮可下载 CSV 和 JSON 两种格式文件
4. 哈希结果与标准 SHA-256 工具（如 `echo -n "hello" | sha256sum`）一致
5. 冒泡排序输出正确的排序结果及正确的交换/比较次数

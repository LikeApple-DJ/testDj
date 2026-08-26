# 三个接口(Helloworld/哈希/冒泡排序) + 前端Tab页面 + 导出功能 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个全栈演示项目，包含三个后端接口（helloworld、SHA-256哈希、冒泡排序）、一个前端 React 页面（含三个 Tab 展示结果）和一个导出功能（CSV/JSON）。

**Architecture:** 前端使用 React + Vite 构建，包含三个 Tab 组件和一个导出按钮；后端使用 Node.js + Express.js 提供 RESTful API 接口，在内存中维护最近一次调用结果用于导出。Vite 开发服务器代理 `/api` 请求到后端 Express 服务。

**Tech Stack:** Node.js 18+, Express.js ^4.18.0, React ^18.2.0, Vite ^5.0.0, CORS ^2.8.5, 原生 CSS

---

## Global Constraints

- Node.js 18+ 运行环境
- 所有 API 响应统一 JSON 格式：`{ code, message, data }`
- 错误响应 code=4xx, message 为具体错误描述, data=null
- 前端端口 5173，后端端口 3000，Vite 代理 `/api` 到后端
- 哈希算法使用 SHA-256（Node.js 内置 crypto 模块）
- 冒泡排序需返回交换次数(swaps)和比较次数(comparisons)
- 导出接口支持 `?format=csv` 和 `?format=json` 两种格式
- 导出文件名格式：`api_export_YYYYMMDD_HHMMSS.{csv|json}`
- 禁止使用额外数据库，所有状态保存在服务端内存中
- 服务端维护三个内存变量，分别记录最近一次各接口调用结果

---

## Task 1: 项目脚手架初始化

**Files:**
- Create: `package.json`
- Create: `vite.config.js`
- Create: `index.html`
- Create: `src/main.jsx`

**Interfaces:**
- Consumes: 无（首个任务）
- Produces: 项目构建配置、HTML 入口、React 挂载入口

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "api-demo-app",
  "version": "1.0.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "concurrently \"npm run server\" \"npm run client\"",
    "server": "node server/index.js",
    "client": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "express": "^4.18.2",
    "cors": "^2.8.5",
    "concurrently": "^8.2.2"
  },
  "devDependencies": {
    "vite": "^5.4.2",
    "@vitejs/plugin-react": "^4.3.1",
    "react": "^18.3.1",
    "react-dom": "^18.3.1"
  }
}
```

- [ ] **Step 2: 创建 vite.config.js**

```javascript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:3000',
        changeOrigin: true,
      },
    },
  },
});
```

- [ ] **Step 3: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>API 演示工具</title>
</head>
<body>
  <div id="root"></div>
  <script type="module" src="/src/main.jsx"></script>
</body>
</html>
```

- [ ] **Step 4: 创建 src/main.jsx**

```jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './App.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

- [ ] **Step 5: 验证脚手架**

Run: `ls -la package.json vite.config.js index.html src/main.jsx`
Expected: 四个文件均存在

---

## Task 2: 后端 Express 服务 — 三个核心接口 + 导出接口 + 内存状态管理

**Files:**
- Create: `server/index.js`

**Interfaces:**
- Consumes: 无（纯后端，不依赖前端模块）
- Produces: 
  - `GET /api/helloworld` → `{ code, message, data: { greeting, timestamp } }`
  - `POST /api/hash` → `{ code, message, data: { input, algorithm, hash } }`
  - `POST /api/sort` → `{ code, message, data: { original, sorted, algorithm, swaps, comparisons } }`
  - `GET /api/export?format=csv|json` → 文件下载（Content-Disposition: attachment）
  - 更新全局内存变量 `lastHelloWorldResult`, `lastHashResult`, `lastSortResult`

- [ ] **Step 1: 创建 server/index.js 文件，包含 Express 初始化和中间件**

```javascript
import express from 'express';
import cors from 'cors';
import crypto from 'crypto';

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

// 内存状态：记录最近一次各接口调用结果
const state = {
  lastHelloWorldResult: null,
  lastHashResult: null,
  lastSortResult: null,
};

// --- 各接口实现在后续步骤中插入 ---

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
```

- [ ] **Step 2: 实现 GET /api/helloworld**

```javascript
app.get('/api/helloworld', (req, res) => {
  const now = new Date();
  const pad = (n) => String(n).padStart(2, '0');
  const timestamp = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;

  const result = {
    greeting: 'Hello World!',
    timestamp,
  };

  state.lastHelloWorldResult = result;

  res.json({
    code: 200,
    message: 'success',
    data: result,
  });
});
```

- [ ] **Step 3: 实现 POST /api/hash**

```javascript
app.post('/api/hash', (req, res) => {
  const { input } = req.body;

  if (input === undefined || input === null || typeof input !== 'string' || input.trim() === '') {
    return res.status(400).json({
      code: 400,
      message: 'input 参数必须为非空字符串',
      data: null,
    });
  }

  const hash = crypto.createHash('sha256').update(input).digest('hex');

  const result = {
    input,
    algorithm: 'SHA-256',
    hash,
  };

  state.lastHashResult = result;

  res.json({
    code: 200,
    message: 'success',
    data: result,
  });
});
```

- [ ] **Step 4: 实现 POST /api/sort**

```javascript
app.post('/api/sort', (req, res) => {
  const { array } = req.body;

  if (!Array.isArray(array) || array.length === 0 || !array.every(Number.isInteger)) {
    return res.status(400).json({
      code: 400,
      message: 'array 参数必须为包含至少一个整数的数组',
      data: null,
    });
  }

  const original = [...array];
  const arr = [...array];
  const n = arr.length;
  let swaps = 0;
  let comparisons = 0;

  // 冒泡排序
  for (let i = 0; i < n - 1; i++) {
    for (let j = 0; j < n - 1 - i; j++) {
      comparisons++;
      if (arr[j] > arr[j + 1]) {
        [arr[j], arr[j + 1]] = [arr[j + 1], arr[j]];
        swaps++;
      }
    }
  }

  const result = {
    original,
    sorted: arr,
    algorithm: 'bubble_sort',
    swaps,
    comparisons,
  };

  state.lastSortResult = result;

  res.json({
    code: 200,
    message: 'success',
    data: result,
  });
});
```

- [ ] **Step 5: 实现 GET /api/export**

```javascript
app.get('/api/export', (req, res) => {
  const format = req.query.format || 'json';

  const exportData = {
    export_time: new Date().toISOString().replace('T', ' ').substring(0, 19),
    helloworld: state.lastHelloWorldResult,
    hash: state.lastHashResult,
    sort: state.lastSortResult,
  };

  const now = new Date();
  const pad = (n) => String(n).padStart(2, '0');
  const fileTimestamp = `${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}_${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`;

  if (format === 'json') {
    const jsonStr = JSON.stringify(exportData, null, 2);
    res.setHeader('Content-Type', 'application/json');
    res.setHeader('Content-Disposition', `attachment; filename="api_export_${fileTimestamp}.json"`);
    return res.send(jsonStr);
  }

  if (format === 'csv') {
    // CSV 头 + 行
    const headers = ['section', 'key', 'value'];
    const rows = [];

    for (const [section, data] of Object.entries(exportData)) {
      if (data === null) {
        rows.push([section, 'status', 'no_data']);
        continue;
      }
      if (typeof data === 'object') {
        for (const [key, value] of Object.entries(data)) {
          const val = typeof value === 'object' ? JSON.stringify(value) : String(value);
          rows.push([section, key, val]);
        }
      } else {
        rows.push([section, 'value', String(data)]);
      }
    }

    const escCsv = (v) => `"${String(v).replace(/"/g, '""')}"`;
    const csvLines = [
      headers.join(','),
      ...rows.map((r) => r.map(escCsv).join(',')),
    ];
    const csvStr = csvLines.join('\n');

    res.setHeader('Content-Type', 'text/csv; charset=utf-8');
    res.setHeader('Content-Disposition', `attachment; filename="api_export_${fileTimestamp}.csv"`);
    return res.send(csvStr);
  }

  res.status(400).json({
    code: 400,
    message: '不支持的导出格式，仅支持 csv 和 json',
    data: null,
  });
});
```

- [ ] **Step 6: 验证后端服务可启动**

Run: `node server/index.js &` 然后 `curl http://localhost:3000/api/helloworld`
Expected: 返回 JSON `{"code":200,"message":"success","data":{"greeting":"Hello World!",...}}`

---

## Task 3: 前端 — App 主组件 + 全局样式

**Files:**
- Create: `src/App.jsx`
- Create: `src/App.css`

**Interfaces:**
- Consumes: React 18, 各子组件从 Task 4-7 引入
- Produces: 顶层应用组件，包含 Tab 切换逻辑和状态管理

- [ ] **Step 1: 创建 src/App.jsx**

```jsx
import React, { useState } from 'react';
import TabBar from './components/TabBar';
import HelloWorldTab from './components/HelloWorldTab';
import HashTab from './components/HashTab';
import SortTab from './components/SortTab';
import ExportButton from './components/ExportButton';

const TABS = [
  { key: 'helloworld', label: 'Hello World' },
  { key: 'hash', label: '哈希算法' },
  { key: 'sort', label: '冒泡排序' },
];

export default function App() {
  const [activeTab, setActiveTab] = useState('helloworld');

  const renderTabContent = () => {
    switch (activeTab) {
      case 'helloworld':
        return <HelloWorldTab />;
      case 'hash':
        return <HashTab />;
      case 'sort':
        return <SortTab />;
      default:
        return null;
    }
  };

  return (
    <div className="app-container">
      <h1 className="app-title">API 演示工具</h1>
      <div className="toolbar">
        <TabBar tabs={TABS} activeTab={activeTab} onTabChange={setActiveTab} />
        <ExportButton />
      </div>
      <div className="tab-content">
        {renderTabContent()}
      </div>
    </div>
  );
}
```

- [ ] **Step 2: 创建 src/App.css**

```css
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen,
    Ubuntu, Cantarell, sans-serif;
  background: #f5f7fa;
  color: #333;
  min-height: 100vh;
}

.app-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 32px 20px;
}

.app-title {
  font-size: 28px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 24px;
  color: #1a1a2e;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.tab-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 24px;
  min-height: 300px;
}

/* Tab 导航 */
.tab-bar {
  display: flex;
  gap: 4px;
  background: #e8ecf1;
  border-radius: 8px;
  padding: 4px;
}

.tab-button {
  padding: 8px 20px;
  border: none;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  transition: all 0.2s;
}

.tab-button:hover {
  color: #333;
}

.tab-button.active {
  background: #fff;
  color: #1a1a2e;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

/* Tab 内容卡片 */
.tab-panel h3 {
  font-size: 18px;
  margin-bottom: 16px;
  color: #1a1a2e;
}

.input-group {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.input-group input {
  flex: 1;
  min-width: 200px;
  padding: 10px 14px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.input-group input:focus {
  border-color: #4a6cf7;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary {
  background: #4a6cf7;
  color: #fff;
}

.btn-primary:hover {
  background: #3b5de7;
}

.btn-primary:disabled {
  background: #a0b4f8;
  cursor: not-allowed;
}

.btn-export {
  background: #10b981;
  color: #fff;
}

.btn-export:hover {
  background: #059669;
}

/* 结果展示 */
.result-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  margin-top: 12px;
}

.result-card pre {
  background: #1e293b;
  color: #e2e8f0;
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
  font-size: 13px;
  line-height: 1.5;
}

.error-message {
  color: #ef4444;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 6px;
  padding: 10px 14px;
  margin-top: 12px;
  font-size: 14px;
}

/* 导出下拉菜单 */
.export-wrapper {
  position: relative;
}

.export-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 4px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  z-index: 100;
  min-width: 120px;
  overflow: hidden;
}

.export-dropdown button {
  display: block;
  width: 100%;
  padding: 10px 16px;
  border: none;
  background: transparent;
  text-align: left;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: background 0.15s;
}

.export-dropdown button:hover {
  background: #f1f5f9;
}

.spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid #e2e8f0;
  border-top-color: #4a6cf7;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
  margin-right: 8px;
  vertical-align: middle;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
```

---

## Task 4: 前端 — TabBar 组件

**Files:**
- Create: `src/components/TabBar.jsx`

**Interfaces:**
- Consumes: `tabs: Array<{key, label}>`, `activeTab: string`, `onTabChange: (key) => void`
- Produces: 渲染 Tab 导航按钮组

- [ ] **Step 1: 创建 src/components/TabBar.jsx**

```jsx
import React from 'react';

export default function TabBar({ tabs, activeTab, onTabChange }) {
  return (
    <div className="tab-bar">
      {tabs.map((tab) => (
        <button
          key={tab.key}
          className={`tab-button ${activeTab === tab.key ? 'active' : ''}`}
          onClick={() => onTabChange(tab.key)}
        >
          {tab.label}
        </button>
      ))}
    </div>
  );
}
```

---

## Task 5: 前端 — HelloWorldTab 组件

**Files:**
- Create: `src/components/HelloWorldTab.jsx`

**Interfaces:**
- Consumes: `GET /api/helloworld` 接口
- Produces: 渲染问候语结果卡片

- [ ] **Step 1: 创建 src/components/HelloWorldTab.jsx**

```jsx
import React, { useState } from 'react';

export default function HelloWorldTab() {
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const handleExecute = async () => {
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const res = await fetch('/api/helloworld');
      const data = await res.json();
      if (data.code === 200) {
        setResult(data.data);
      } else {
        setError(data.message);
      }
    } catch (err) {
      setError('请求失败：' + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="tab-panel">
      <h3>Hello World</h3>
      <p style={{ marginBottom: 16, color: '#666' }}>点击下方按钮获取问候消息</p>
      <button
        className="btn btn-primary"
        onClick={handleExecute}
        disabled={loading}
      >
        {loading ? <><span className="spinner" />请求中...</> : '执行'}
      </button>

      {error && <div className="error-message">{error}</div>}

      {result && (
        <div className="result-card">
          <pre>{JSON.stringify(result, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}
```

---

## Task 6: 前端 — HashTab 组件

**Files:**
- Create: `src/components/HashTab.jsx`

**Interfaces:**
- Consumes: `POST /api/hash` 接口，发送 `{ input: string }`
- Produces: 渲染哈希结果卡片

- [ ] **Step 1: 创建 src/components/HashTab.jsx**

```jsx
import React, { useState } from 'react';

export default function HashTab() {
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const handleExecute = async () => {
    if (!input.trim()) {
      setError('请输入待哈希的字符串');
      return;
    }
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const res = await fetch('/api/hash', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ input: input.trim() }),
      });
      const data = await res.json();
      if (data.code === 200) {
        setResult(data.data);
      } else {
        setError(data.message);
      }
    } catch (err) {
      setError('请求失败：' + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="tab-panel">
      <h3>SHA-256 哈希算法</h3>
      <div className="input-group">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="输入待哈希的字符串..."
          onKeyDown={(e) => e.key === 'Enter' && handleExecute()}
        />
        <button
          className="btn btn-primary"
          onClick={handleExecute}
          disabled={loading}
        >
          {loading ? <><span className="spinner" />计算中...</> : '执行哈希'}
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {result && (
        <div className="result-card">
          <pre>{JSON.stringify(result, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}
```

---

## Task 7: 前端 — SortTab 组件

**Files:**
- Create: `src/components/SortTab.jsx`

**Interfaces:**
- Consumes: `POST /api/sort` 接口，发送 `{ array: number[] }`
- Produces: 渲染排序结果卡片

- [ ] **Step 1: 创建 src/components/SortTab.jsx**

```jsx
import React, { useState } from 'react';

export default function SortTab() {
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const handleExecute = async () => {
    const trimmed = input.trim();
    if (!trimmed) {
      setError('请输入逗号分隔的数字，例如：5, 3, 8, 1, 9, 2');
      return;
    }
    const parts = trimmed.split(',').map((s) => s.trim()).filter(Boolean);
    const array = parts.map(Number);
    if (array.length === 0 || array.some(isNaN)) {
      setError('请输入有效的整数，逗号分隔，例如：5, 3, 8, 1, 9, 2');
      return;
    }

    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const res = await fetch('/api/sort', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ array }),
      });
      const data = await res.json();
      if (data.code === 200) {
        setResult(data.data);
      } else {
        setError(data.message);
      }
    } catch (err) {
      setError('请求失败：' + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="tab-panel">
      <h3>冒泡排序</h3>
      <div className="input-group">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="输入数字，逗号分隔，如：5, 3, 8, 1, 9, 2"
          onKeyDown={(e) => e.key === 'Enter' && handleExecute()}
        />
        <button
          className="btn btn-primary"
          onClick={handleExecute}
          disabled={loading}
        >
          {loading ? <><span className="spinner" />排序中...</> : '执行排序'}
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {result && (
        <div className="result-card">
          <pre>{JSON.stringify(result, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}
```

---

## Task 8: 前端 — ExportButton 组件

**Files:**
- Create: `src/components/ExportButton.jsx`

**Interfaces:**
- Consumes: `GET /api/export?format=csv|json` 接口
- Produces: 渲染导出按钮及格式下拉菜单，触发文件下载

- [ ] **Step 1: 创建 src/components/ExportButton.jsx**

```jsx
import React, { useState, useRef, useEffect } from 'react';

export default function ExportButton() {
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const dropdownRef = useRef(null);

  // 点击外部关闭下拉菜单
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleExport = async (format) => {
    setOpen(false);
    setLoading(true);
    try {
      const res = await fetch(`/api/export?format=${format}`);
      if (!res.ok) {
        const errData = await res.json();
        alert('导出失败：' + (errData.message || '未知错误'));
        return;
      }
      // 触发文件下载
      const blob = await res.blob();
      const disposition = res.headers.get('Content-Disposition') || '';
      const match = disposition.match(/filename="?(.+?)"?$/);
      const filename = match ? match[1] : `api_export.${format}`;
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (err) {
      alert('导出请求失败：' + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="export-wrapper" ref={dropdownRef}>
      <button
        className="btn btn-export"
        onClick={() => setOpen(!open)}
        disabled={loading}
      >
        {loading ? '导出中...' : '导出 ▼'}
      </button>
      {open && (
        <div className="export-dropdown">
          <button onClick={() => handleExport('json')}>导出 JSON</button>
          <button onClick={() => handleExport('csv')}>导出 CSV</button>
        </div>
      )}
    </div>
  );
}
```

---

## Task 9: 安装依赖并验证全栈运行

**Files:**
- Modify: 无（仅运行命令）

- [ ] **Step 1: 安装 npm 依赖**

Run: `npm install`
Expected: 所有依赖安装成功，无报错

- [ ] **Step 2: 启动后端服务并验证所有接口**

Run: `node server/index.js &` (后台启动)
然后依次验证：

```bash
# 1. Hello World
curl http://localhost:3000/api/helloworld
# 期望: {"code":200,"message":"success","data":{"greeting":"Hello World!",...}}

# 2. 哈希算法
curl -X POST http://localhost:3000/api/hash -H "Content-Type: application/json" -d '{"input":"hello"}'
# 期望: code=200, hash 与 echo -n "hello" | sha256sum 一致

# 3. 冒泡排序
curl -X POST http://localhost:3000/api/sort -H "Content-Type: application/json" -d '{"array":[5,3,8,1,9,2]}'
# 期望: sorted=[1,2,3,5,8,9], swaps=10, comparisons=15

# 4. 导出 JSON
curl -o /tmp/export.json http://localhost:3000/api/export?format=json
# 期望: 文件下载成功，包含前三个接口的结果

# 5. 导出 CSV
curl -o /tmp/export.csv http://localhost:3000/api/export?format=csv
# 期望: CSV 文件下载成功

# 6. 错误处理验证 — 哈希接口输入为空
curl -X POST http://localhost:3000/api/hash -H "Content-Type: application/json" -d '{"input":""}'
# 期望: code=400, message="input 参数必须为非空字符串"

# 7. 错误处理验证 — 排序接口输入非整数数组
curl -X POST http://localhost:3000/api/sort -H "Content-Type: application/json" -d '{"array":["a","b"]}'
# 期望: code=400, message="array 参数必须为包含至少一个整数的数组"

# 8. 错误处理验证 — 导出格式不支持
curl http://localhost:3000/api/export?format=xml
# 期望: code=400, message="不支持的导出格式，仅支持 csv 和 json"
```

- [ ] **Step 3: 验证前端构建**

Run: `npx vite build`
Expected: 构建成功，生成 dist/ 目录

---

## 自检清单

### 1. 需求覆盖检查

| 需求 | 对应 Task | 状态 |
|------|-----------|------|
| GET /api/helloworld 接口 | Task 2 Step 2 | ✅ |
| POST /api/hash 接口（SHA-256） | Task 2 Step 3 | ✅ |
| POST /api/sort 接口（冒泡排序） | Task 2 Step 4 | ✅ |
| 统一 JSON 响应格式 {code, message, data} | Task 2 全部步骤 | ✅ |
| 错误处理（空输入、非法格式） | Task 2 Step 3/4/5 | ✅ |
| 前端页面含三个 Tab | Task 3 + Task 4-7 | ✅ |
| Tab 1: Hello World | Task 5 | ✅ |
| Tab 2: 哈希算法 | Task 6 | ✅ |
| Tab 3: 冒泡排序 | Task 7 | ✅ |
| 导出按钮（CSV/JSON 下拉） | Task 8 | ✅ |
| GET /api/export 导出接口 | Task 2 Step 5 | ✅ |
| 内存状态管理 | Task 2 Step 1（state 变量） | ✅ |
| 项目脚手架（package.json, vite.config.js, index.html） | Task 1 | ✅ |
| 全局样式 | Task 3 Step 2 | ✅ |

### 2. 占位符检查

- 无 "TBD", "TODO", "implement later", "fill in details" 等占位符
- 所有代码块包含完整实现
- 所有步骤有具体命令和期望输出
- 所有类型签名和接口定义一致

### 3. 类型一致性检查

- `GET /api/helloworld` 返回 `{ greeting, timestamp }` — 一致
- `POST /api/hash` 接收 `{ input: string }`，返回 `{ input, algorithm: "SHA-256", hash }` — 一致
- `POST /api/sort` 接收 `{ array: number[] }`，返回 `{ original, sorted, algorithm: "bubble_sort", swaps, comparisons }` — 一致
- `GET /api/export?format=csv|json` 返回文件下载 — 一致
- 前端组件名与 import 路径一致：`TabBar`, `HelloWorldTab`, `HashTab`, `SortTab`, `ExportButton` — 一致
- 内存变量 `lastHelloWorldResult`, `lastHashResult`, `lastSortResult` 在接口中统一引用 — 一致
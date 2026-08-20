# HelloWorld 商业化 T1 分别写 实施计划

> **前置设计文档**：`testDj-main/.agents/20260820-helloworld-商业化T1_分别写/design.md`  
> **计划编号**：2026-08-20-helloworld-商业化T1_分别写  
> **目标**：从零搭建可运行的前后端工程，完成 3 个 Tab 展示 + 导出功能。

## Goal

在 `testDj-main` 仓库中实现一个完整演示功能：

- 后端提供 4 个接口：
  - `POST /api/v1/helloworld`
  - `POST /api/v1/hash`
  - `POST /api/v1/bubble-sort`
  - `POST /api/v1/export`
- 前端新增一个页面，包含 3 个 Tab 分别展示上述三个功能接口的执行结果。
- 页面提供“导出”按钮，调用后端导出接口下载当前 Tab 的展示结果。

## Architecture

- 后端：Node.js + Express，单服务承载 4 个 REST 接口，统一 JSON 响应格式。
- 前端：React 18 + Vite，单页面 + 3 个 Tab 组件，通过 `fetch` 调用后端接口。
- 导出：前端将当前 Tab 数据封装后 POST 到 `/api/v1/export`，后端返回文件流，前端通过 `Blob` 触发下载。
- 目录结构：后端 `server/`、前端 `client/`，根目录提供统一启动脚本。

## Tech Stack

- 后端：Node.js 18+、Express 4.x、crypto（内置）
- 前端：React 18、Vite 5、ES Modules
- 包管理器：npm
- 端口：后端默认 `3001`，前端开发服务器默认 `5173`，通过 Vite proxy 转发 `/api` 到后端。

## Global Constraints

- 不做持久化存储，结果实时计算或内存缓存。
- 不做用户权限与登录鉴权。
- 接口统一响应格式：

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

- 导出文件默认 `.txt`，请求参数 `format=json` 时返回 `.json`。
- 所有新增代码文件必须落在 `testDj-main/` 目录内，不得污染 `testDJnew-main`。

---

## Task 1: 初始化后端工程

**Files:**
- Create: `testDj-main/server/package.json`
- Create: `testDj-main/server/app.js`
- Create: `testDj-main/server/.env`

**Interfaces:**
- Produces: Express app 监听 `PORT`（默认 3001），提供 `/api/v1` 路由挂载点。

### Steps

- [ ] **Step 1: 创建后端 package.json**

```json
{
  "name": "helloworld-t1-server",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "start": "node app.js",
    "dev": "node --watch app.js"
  },
  "dependencies": {
    "express": "^4.19.2",
    "cors": "^2.8.5"
  }
}
```

- [ ] **Step 2: 安装依赖**

```bash
cd testDj-main/server
npm install
```

Expected: `node_modules/`、 `package-lock.json` 生成，无报错。

- [ ] **Step 3: 编写最小 Express 启动脚本**

`testDj-main/server/app.js`:

```js
import express from 'express';
import cors from 'cors';

const app = express();
const PORT = process.env.PORT || 3001;

app.use(cors());
app.use(express.json());

app.get('/api/v1/health', (req, res) => {
  res.json({ code: 0, message: 'ok', data: null });
});

app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});
```

- [ ] **Step 4: 启动并验证**

```bash
npm run start
```

在另一终端执行：

```bash
curl http://localhost:3001/api/v1/health
```

Expected:

```json
{"code":0,"message":"ok","data":null}
```

- [ ] **Step 5: 提交（只读仓库，不实际 commit）**

---

## Task 2: 实现 HelloWorld 接口

**Files:**
- Create: `testDj-main/server/routes/helloworld.js`
- Modify: `testDj-main/server/app.js`

**Interfaces:**
- Consumes: Express app 的 `/api/v1` 路由挂载点。
- Produces: `POST /api/v1/helloworld` 返回 `{ greeting: string }`。

### Steps

- [ ] **Step 1: 创建路由文件**

`testDj-main/server/routes/helloworld.js`:

```js
import { Router } from 'express';

const router = Router();

router.post('/', (req, res) => {
  const { name = 'World' } = req.body || {};
  res.json({
    code: 0,
    message: 'ok',
    data: { greeting: `Hello, ${name}!` }
  });
});

export default router;
```

- [ ] **Step 2: 挂载路由**

在 `testDj-main/server/app.js` 中新增：

```js
import helloworldRouter from './routes/helloworld.js';

app.use('/api/v1/helloworld', helloworldRouter);
```

- [ ] **Step 3: 启动服务并测试**

```bash
curl -X POST http://localhost:3001/api/v1/helloworld \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice"}'
```

Expected:

```json
{"code":0,"message":"ok","data":{"greeting":"Hello, Alice!"}}
```

---

## Task 3: 实现 Hash 算法接口

**Files:**
- Create: `testDj-main/server/routes/hash.js`
- Modify: `testDj-main/server/app.js`

**Interfaces:**
- Consumes: Express app 的 `/api/v1` 路由挂载点。
- Produces: `POST /api/v1/hash` 返回 `{ input, algorithm, hash }`。

### Steps

- [ ] **Step 1: 创建路由文件**

`testDj-main/server/routes/hash.js`:

```js
import { Router } from 'express';
import crypto from 'crypto';

const router = Router();
const SUPPORTED = ['MD5', 'SHA-256'];

router.post('/', (req, res) => {
  const { text = '', algorithm = 'SHA-256' } = req.body || {};

  if (!SUPPORTED.includes(algorithm)) {
    return res.status(400).json({
      code: 400,
      message: `Unsupported algorithm. Use one of: ${SUPPORTED.join(', ')}`,
      data: null
    });
  }

  const hash = crypto.createHash(algorithm.toLowerCase()).update(text).digest('hex');

  res.json({
    code: 0,
    message: 'ok',
    data: { input: text, algorithm, hash }
  });
});

export default router;
```

- [ ] **Step 2: 挂载路由**

在 `testDj-main/server/app.js` 中新增：

```js
import hashRouter from './routes/hash.js';

app.use('/api/v1/hash', hashRouter);
```

- [ ] **Step 3: 测试接口**

```bash
curl -X POST http://localhost:3001/api/v1/hash \
  -H "Content-Type: application/json" \
  -d '{"text":"hello world","algorithm":"SHA-256"}'
```

Expected（hash 值 truncated）：

```json
{"code":0,"message":"ok","data":{"input":"hello world","algorithm":"SHA-256","hash":"b94d..."}}
```

---

## Task 4: 实现冒泡排序接口

**Files:**
- Create: `testDj-main/server/routes/bubbleSort.js`
- Modify: `testDj-main/server/app.js`

**Interfaces:**
- Consumes: Express app 的 `/api/v1` 路由挂载点。
- Produces: `POST /api/v1/bubble-sort` 返回 `{ input, output, steps }`。

### Steps

- [ ] **Step 1: 创建排序逻辑与路由**

`testDj-main/server/routes/bubbleSort.js`:

```js
import { Router } from 'express';

const router = Router();

function bubbleSort(arr) {
  const list = [...arr];
  let steps = 0;
  for (let i = 0; i < list.length - 1; i++) {
    for (let j = 0; j < list.length - 1 - i; j++) {
      steps++;
      if (list[j] > list[j + 1]) {
        [list[j], list[j + 1]] = [list[j + 1], list[j]];
      }
    }
  }
  return { output: list, steps };
}

router.post('/', (req, res) => {
  const { array = [] } = req.body || {};

  if (!Array.isArray(array) || !array.every((n) => typeof n === 'number')) {
    return res.status(400).json({
      code: 400,
      message: 'array must be an array of numbers',
      data: null
    });
  }

  const { output, steps } = bubbleSort(array);

  res.json({
    code: 0,
    message: 'ok',
    data: { input: array, output, steps }
  });
});

export default router;
```

- [ ] **Step 2: 挂载路由**

在 `testDj-main/server/app.js` 中新增：

```js
import bubbleSortRouter from './routes/bubbleSort.js';

app.use('/api/v1/bubble-sort', bubbleSortRouter);
```

- [ ] **Step 3: 测试接口**

```bash
curl -X POST http://localhost:3001/api/v1/bubble-sort \
  -H "Content-Type: application/json" \
  -d '{"array":[5,3,8,1,2]}'
```

Expected:

```json
{"code":0,"message":"ok","data":{"input":[5,3,8,1,2],"output":[1,2,3,5,8],"steps":8}}
```

---

## Task 5: 实现导出接口

**Files:**
- Create: `testDj-main/server/routes/export.js`
- Modify: `testDj-main/server/app.js`

**Interfaces:**
- Consumes: 前端当前 Tab 类型及结果数据。
- Produces: `POST /api/v1/export` 返回 `Content-Disposition: attachment` 文件流。

### Steps

- [ ] **Step 1: 创建导出路由**

`testDj-main/server/routes/export.js`:

```js
import { Router } from 'express';

const router = Router();

router.post('/', (req, res) => {
  const { type = 'helloworld', data = {}, format = 'txt' } = req.body || {};

  if (!['helloworld', 'hash', 'bubble-sort'].includes(type)) {
    return res.status(400).json({
      code: 400,
      message: 'Unsupported export type',
      data: null
    });
  }

  let content = '';
  let extension = 'txt';
  let contentType = 'text/plain';

  if (format === 'json') {
    extension = 'json';
    contentType = 'application/json';
    content = JSON.stringify({ type, ...data }, null, 2);
  } else {
    extension = 'txt';
    contentType = 'text/plain';
    content = `Type: ${type}\n`;
    for (const [key, value] of Object.entries(data)) {
      content += `${key}: ${JSON.stringify(value)}\n`;
    }
  }

  const filename = `result-${type}.${extension}`;
  res.setHeader('Content-Disposition', `attachment; filename="${filename}"`);
  res.setHeader('Content-Type', contentType);
  res.send(content);
});

export default router;
```

- [ ] **Step 2: 挂载路由**

在 `testDj-main/server/app.js` 中新增：

```js
import exportRouter from './routes/export.js';

app.use('/api/v1/export', exportRouter);
```

- [ ] **Step 3: 测试导出接口**

```bash
curl -X POST http://localhost:3001/api/v1/export \
  -H "Content-Type: application/json" \
  -d '{"type":"helloworld","data":{"greeting":"Hello, World!"}}' \
  -D - --output /tmp/result.txt
```

Expected response headers 包含：

```
Content-Disposition: attachment; filename="result-helloworld.txt"
Content-Type: text/plain
```

---

## Task 6: 初始化前端工程

**Files:**
- Create: `testDj-main/client/package.json`
- Create: `testDj-main/client/index.html`
- Create: `testDj-main/client/vite.config.js`

**Interfaces:**
- Produces: Vite 开发服务器，默认 `http://localhost:5173`，并通过 proxy 转发 `/api` 到后端。

### Steps

- [ ] **Step 1: 创建前端 package.json**

`testDj-main/client/package.json`:

```json
{
  "name": "helloworld-t1-client",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.3.1",
    "vite": "^5.3.1"
  }
}
```

- [ ] **Step 2: 安装依赖**

```bash
cd testDj-main/client
npm install
```

Expected: `node_modules/`、`package-lock.json` 生成，无报错。

- [ ] **Step 3: 创建 Vite 配置**

`testDj-main/client/vite.config.js`:

```js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:3001',
        changeOrigin: true
      }
    }
  }
});
```

- [ ] **Step 4: 创建入口 HTML**

`testDj-main/client/index.html`:

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>HelloWorld 商业化 T1</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.jsx"></script>
  </body>
</html>
```

---

## Task 7: 实现前端入口与样式

**Files:**
- Create: `testDj-main/client/src/main.jsx`
- Create: `testDj-main/client/src/App.jsx`
- Create: `testDj-main/client/src/index.css`

**Interfaces:**
- Produces: React 应用根组件，渲染 3 个 Tab 的页面。

### Steps

- [ ] **Step 1: 创建入口文件**

`testDj-main/client/src/main.jsx`:

```jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

- [ ] **Step 2: 创建样式文件**

`testDj-main/client/src/index.css`:

```css
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  margin: 0;
  padding: 24px;
  background: #f5f5f5;
}

.container {
  max-width: 720px;
  margin: 0 auto;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h1 {
  margin-top: 0;
}

.tabs {
  display: flex;
  gap: 8px;
  border-bottom: 2px solid #eee;
  margin-bottom: 16px;
}

.tab {
  padding: 8px 16px;
  cursor: pointer;
  border: none;
  background: transparent;
  font-size: 16px;
}

.tab.active {
  border-bottom: 2px solid #1890ff;
  color: #1890ff;
  font-weight: 600;
}

.section {
  margin-bottom: 16px;
}

input[type='text'] {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
  margin-bottom: 8px;
}

select {
  padding: 8px;
  margin-bottom: 8px;
}

button {
  padding: 8px 16px;
  margin-right: 8px;
  cursor: pointer;
}

.result {
  margin-top: 16px;
  padding: 12px;
  background: #fafafa;
  border: 1px solid #eee;
  border-radius: 4px;
  white-space: pre-wrap;
}
```

---

## Task 8: 实现 Tab 页面与接口调用

**Files:**
- Create: `testDj-main/client/src/App.jsx`
- Create: `testDj-main/client/src/api.js`

**Interfaces:**
- Consumes: `/api/v1/helloworld`, `/api/v1/hash`, `/api/v1/bubble-sort`, `/api/v1/export`。
- Produces: 可切换的 3 个 Tab UI。

### Steps

- [ ] **Step 1: 创建 API 调用模块**

`testDj-main/client/src/api.js`:

```js
const API_BASE = '/api/v1';

async function post(path, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });

  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `HTTP ${res.status}`);
  }

  return res.json();
}

export function helloWorld(name) {
  return post('/helloworld', { name });
}

export function hash(text, algorithm = 'SHA-256') {
  return post('/hash', { text, algorithm });
}

export function bubbleSort(array) {
  return post('/bubble-sort', { array });
}

export function downloadExport(type, data, format = 'txt') {
  return fetch(`${API_BASE}/export`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ type, data, format })
  });
}
```

- [ ] **Step 2: 创建 App 组件**

`testDj-main/client/src/App.jsx`:

```jsx
import { useState } from 'react';
import { helloWorld, hash, bubbleSort, downloadExport } from './api';

const TABS = [
  { key: 'helloworld', label: 'HelloWorld' },
  { key: 'hash', label: 'Hash 算法' },
  { key: 'bubble-sort', label: '冒泡排序' }
];

export default function App() {
  const [active, setActive] = useState('helloworld');
  const [loading, setLoading] = useState(false);

  const [helloName, setHelloName] = useState('World');
  const [helloResult, setHelloResult] = useState(null);

  const [hashText, setHashText] = useState('hello world');
  const [hashAlgorithm, setHashAlgorithm] = useState('SHA-256');
  const [hashResult, setHashResult] = useState(null);

  const [arrayText, setArrayText] = useState('5,3,8,1,2');
  const [sortResult, setSortResult] = useState(null);

  async function handleHello() {
    setLoading(true);
    const res = await helloWorld(helloName);
    setHelloResult(res.data);
    setLoading(false);
  }

  async function handleHash() {
    setLoading(true);
    const res = await hash(hashText, hashAlgorithm);
    setHashResult(res.data);
    setLoading(false);
  }

  async function handleSort() {
    const arr = arrayText.split(',').map((s) => Number(s.trim()));
    setLoading(true);
    const res = await bubbleSort(arr);
    setSortResult(res.data);
    setLoading(false);
  }

  function currentResult() {
    if (active === 'helloworld') return helloResult;
    if (active === 'hash') return hashResult;
    return sortResult;
  }

  async function handleExport(format = 'txt') {
    const data = currentResult();
    if (!data) return;
    const res = await downloadExport(active, data, format);
    const blob = await res.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `result-${active}.${format === 'json' ? 'json' : 'txt'}`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url);
  }

  return (
    <div className="container">
      <h1>HelloWorld 商业化 T1</h1>
      <div className="tabs">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            className={`tab ${active === tab.key ? 'active' : ''}`}
            onClick={() => setActive(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {active === 'helloworld' && (
        <div className="section">
          <label>名称：</label>
          <input value={helloName} onChange={(e) => setHelloName(e.target.value)} />
          <button onClick={handleHello} disabled={loading}>执行</button>
          {helloResult && (
            <div className="result">{helloResult.greeting}</div>
          )}
        </div>
      )}

      {active === 'hash' && (
        <div className="section">
          <label>文本：</label>
          <input value={hashText} onChange={(e) => setHashText(e.target.value)} />
          <select value={hashAlgorithm} onChange={(e) => setHashAlgorithm(e.target.value)}>
            <option value="SHA-256">SHA-256</option>
            <option value="MD5">MD5</option>
          </select>
          <button onClick={handleHash} disabled={loading}>执行</button>
          {hashResult && (
            <div className="result">
              输入：{hashResult.input}
              算法：{hashResult.algorithm}
              Hash：{hashResult.hash}
            </div>
          )}
        </div>
      )}

      {active === 'bubble-sort' && (
        <div className="section">
          <label>数组（逗号分隔）：</label>
          <input value={arrayText} onChange={(e) => setArrayText(e.target.value)} />
          <button onClick={handleSort} disabled={loading}>执行</button>
          {sortResult && (
            <div className="result">
              原始：{JSON.stringify(sortResult.input)}
              结果：{JSON.stringify(sortResult.output)}
              步数：{sortResult.steps}
            </div>
          )}
        </div>
      )}

      <button onClick={() => handleExport('txt')} disabled={!currentResult()}>
        导出为 TXT
      </button>
      <button onClick={() => handleExport('json')} disabled={!currentResult()}>
        导出为 JSON
      </button>
    </div>
  );
}
```

---

## Task 9: 集成测试

**Files:**
- None (manual verification)

### Steps

- [ ] **Step 1: 启动后端**

```bash
cd testDj-main/server
npm run start
```

- [ ] **Step 2: 启动前端**

```bash
cd testDj-main/client
npm run dev
```

- [ ] **Step 3: 浏览器访问**

打开 `http://localhost:5173`，验证：

1. 页面显示 3 个 Tab：HelloWorld、Hash 算法、冒泡排序。
2. HelloWorld Tab 输入名称后点击执行，显示问候语。
3. Hash 算法 Tab 选择算法、输入文本后点击执行，显示 hash 值。
4. 冒泡排序 Tab 输入逗号分隔数字后点击执行，显示排序结果和步数。
5. 切换 Tab 后，导出按钮可用，点击后下载当前 Tab 的结果文件。

---

## Self-Review

### 1. Spec Coverage

| 需求 | 对应任务 |
|------|----------|
| 后端 HelloWorld 接口 | Task 2 |
| 后端 Hash 算法接口 | Task 3 |
| 后端冒泡排序接口 | Task 4 |
| 后端导出接口 | Task 5 |
| 前端 3 个 Tab 页面 | Task 6-8 |
| 导出按钮下载当前结果 | Task 5 + Task 8 |

### 2. Placeholder Scan

- 无 `TBD`、`TODO`、空实现。
- 所有接口均有具体请求/响应示例。
- 所有代码块均为可运行内容。

### 3. Type & Naming Consistency

- 后端路由统一挂载在 `/api/v1` 下。
- 前端 API 模块函数名与后端路由一一对应。
- 导出接口 `type` 字段与 Tab `key` 保持一致。

### 4. 跨仓对齐点

- 当前实现仅在 `testDj-main`。
- `testDJnew-main` 不参与本阶段，避免重复实现。
- 若后续需要在 `testDJnew-main` 同步，应复用本计划中的接口契约。

---

## 执行方式选择

**Plan complete and saved to `testDj-main/docs/superpowers/plans/2026-08-20-helloworld-商业化T1_分别写.md`.**

Two execution options:

1. **Subagent-Driven (recommended)** — dispatch a fresh subagent per task, review between tasks.
2. **Inline Execution** — execute tasks in this session using `executing-plans`.

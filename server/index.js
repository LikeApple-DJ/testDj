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

// GET /api/helloworld
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

// POST /api/hash
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

// POST /api/sort
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

// GET /api/export
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

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
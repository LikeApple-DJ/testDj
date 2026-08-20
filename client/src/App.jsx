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
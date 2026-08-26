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
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
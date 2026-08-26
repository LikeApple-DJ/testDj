import React, { useState, useRef, useEffect } from 'react';

export default function ExportButton() {
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const dropdownRef = useRef(null);

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
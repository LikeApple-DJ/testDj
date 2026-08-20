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
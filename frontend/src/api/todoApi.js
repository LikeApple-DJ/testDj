const API_BASE = 'http://localhost:8080/api/todos';

export async function createTodo({ name, description }) {
  const response = await fetch(API_BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, description }),
  });
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || '创建失败');
  }
  return response.json();
}

export async function listTodos() {
  const response = await fetch(API_BASE);
  if (!response.ok) {
    throw new Error('获取列表失败');
  }
  return response.json();
}
import { useState, useEffect, useCallback } from 'react';
import TodoForm from './components/TodoForm';
import TodoList from './components/TodoList';
import { listTodos } from './api/todoApi';
import './App.css';

export default function App() {
  const [todos, setTodos] = useState([]);

  const fetchTodos = useCallback(async () => {
    try {
      const data = await listTodos();
      setTodos(data);
    } catch {
      // 静默失败，列表保持当前状态
    }
  }, []);

  useEffect(() => {
    fetchTodos();
  }, [fetchTodos]);

  const handleCreated = () => {
    fetchTodos();
  };

  return (
    <div className="app">
      <h1 className="app__title">日常待办事项</h1>
      <TodoForm onCreated={handleCreated} />
      <TodoList todos={todos} />
    </div>
  );
}
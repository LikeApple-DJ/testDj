import { useState } from 'react';
import { createTodo } from '../api/todoApi';
import './TodoForm.css';

export default function TodoForm({ onCreated }) {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!name.trim()) {
      setError('事项名称不能为空');
      return;
    }

    setSubmitting(true);
    try {
      const created = await createTodo({ name: name.trim(), description: description.trim() });
      setName('');
      setDescription('');
      onCreated(created);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="todo-form" onSubmit={handleSubmit}>
      <div className="todo-form__field">
        <label htmlFor="todo-name">事项名称</label>
        <input
          id="todo-name"
          type="text"
          maxLength={255}
          placeholder="输入待办事项名称"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      </div>
      <div className="todo-form__field">
        <label htmlFor="todo-desc">描述</label>
        <textarea
          id="todo-desc"
          maxLength={5000}
          placeholder="输入描述（选填）"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </div>
      {error && <div className="todo-form__error">{error}</div>}
      <button type="submit" className="todo-form__submit" disabled={submitting}>
        {submitting ? '提交中...' : '新增待办'}
      </button>
    </form>
  );
}
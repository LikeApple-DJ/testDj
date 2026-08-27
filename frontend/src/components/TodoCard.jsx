import './TodoList.css';

export default function TodoCard({ todo }) {
  const formatTime = (isoString) => {
    const date = new Date(isoString);
    const pad = (n) => String(n).padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
  };

  return (
    <div className="todo-card">
      <div className="todo-card__name">📋 {todo.name}</div>
      {todo.description && (
        <div className="todo-card__description">{todo.description}</div>
      )}
      <div className="todo-card__time">{formatTime(todo.createdAt)}</div>
    </div>
  );
}
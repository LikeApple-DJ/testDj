import TodoCard from './TodoCard';
import './TodoList.css';

export default function TodoList({ todos }) {
  if (!todos || todos.length === 0) {
    return (
      <div className="todo-list">
        <div className="todo-list__empty">暂无待办事项，快去添加一个吧！</div>
      </div>
    );
  }

  return (
    <div className="todo-list">
      {todos.map((todo) => (
        <TodoCard key={todo.id} todo={todo} />
      ))}
    </div>
  );
}
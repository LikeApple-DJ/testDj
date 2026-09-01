import { Todo, CreateTodoInput } from '../src/models/todo';

describe('Todo model (type-level)', () => {
  it('accepts a valid CreateTodoInput', () => {
    const input: CreateTodoInput = { name: 'Buy groceries', description: 'Milk, eggs, bread' };
    expect(input.name).toBe('Buy groceries');
    expect(input.description).toBe('Milk, eggs, bread');
  });

  it('accepts CreateTodoInput without description', () => {
    const input: CreateTodoInput = { name: 'Walk the dog' };
    expect(input.name).toBe('Walk the dog');
    expect(input.description).toBeUndefined();
  });

  it('has correct Todo shape', () => {
    const todo: Todo = {
      id: 1,
      name: 'Test',
      description: null,
      createdAt: '2026-09-01T00:00:00.000Z',
    };
    expect(todo.id).toBe(1);
    expect(todo.createdAt).toBe('2026-09-01T00:00:00.000Z');
  });
});
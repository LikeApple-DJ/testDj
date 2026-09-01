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

import Database from 'better-sqlite3';
import { TodoRepository } from '../src/storage/todo-repository';
import * as fs from 'fs';
import * as path from 'path';

describe('TodoRepository', () => {
  const testDbPath = path.join(__dirname, '..', 'data', 'test-todo.db');
  let repo: TodoRepository;

  beforeEach(() => {
    const dir = path.dirname(testDbPath);
    if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
    if (fs.existsSync(testDbPath)) fs.unlinkSync(testDbPath);
    repo = new TodoRepository(testDbPath);
  });

  afterEach(() => {
    if (fs.existsSync(testDbPath)) fs.unlinkSync(testDbPath);
  });

  it('creates a todo and returns it with id and createdAt', () => {
    const result = repo.create({ name: 'Buy groceries', description: 'Milk, eggs' });
    expect(result.id).toBe(1);
    expect(result.name).toBe('Buy groceries');
    expect(result.description).toBe('Milk, eggs');
    expect(result.createdAt).toEqual(expect.any(String));
    expect(new Date(result.createdAt).getTime()).not.toBeNaN();
  });

  it('creates a todo with null description when omitted', () => {
    const result = repo.create({ name: 'Walk the dog' });
    expect(result.description).toBeNull();
  });
});

import request from 'supertest';
import { createApp } from '../src/app';

describe('POST /api/todos', () => {
  const testDbPath = path.join(__dirname, '..', 'data', 'test-api-todo.db');
  let app: ReturnType<typeof createApp>;

  beforeEach(() => {
    const dir = path.dirname(testDbPath);
    if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
    if (fs.existsSync(testDbPath)) fs.unlinkSync(testDbPath);
    const repo = new TodoRepository(testDbPath);
    app = createApp(repo);
  });

  afterEach(() => {
    if (fs.existsSync(testDbPath)) fs.unlinkSync(testDbPath);
  });

  it('returns 201 with the created todo on valid input', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ name: 'Buy groceries', description: 'Milk, eggs' })
      .expect('Content-Type', /json/)
      .expect(201);

    expect(res.body).toMatchObject({
      id: 1,
      name: 'Buy groceries',
      description: 'Milk, eggs',
    });
    expect(res.body.createdAt).toEqual(expect.any(String));
  });

  it('returns 201 with null description when omitted', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ name: 'Walk the dog' })
      .expect(201);

    expect(res.body.description).toBeNull();
  });

  it('returns 400 when name is missing', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ description: 'No name' })
      .expect(400);

    expect(res.body.error).toContain('name');
  });

  it('returns 400 when name is empty', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ name: '' })
      .expect(400);

    expect(res.body.error).toContain('name');
  });

  it('returns 400 when name exceeds 200 characters', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ name: 'x'.repeat(201) })
      .expect(400);

    expect(res.body.error).toContain('name');
  });

  it('returns 400 when description exceeds 2000 characters', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ name: 'Valid', description: 'x'.repeat(2001) })
      .expect(400);

    expect(res.body.error).toContain('description');
  });
});
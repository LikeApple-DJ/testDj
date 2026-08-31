const request = require('supertest');
const { createDb } = require('../src/db');
const { createApp } = require('../src/app');

function setup() {
  const db = createDb(':memory:');
  const app = createApp(db);
  return { db, app };
}

describe('POST /todos', () => {
  let db, app;
  beforeEach(() => {
    ({ db, app } = setup());
  });
  afterEach(() => db.close());

  test('creates a todo with name and description, returns 201', async () => {
    const res = await request(app)
      .post('/todos')
      .send({ name: '买牛奶', description: '去超市买两盒牛奶' })
      .expect(201);
    expect(res.body.id).toEqual(expect.any(Number));
    expect(res.body.name).toBe('买牛奶');
    expect(res.body.description).toBe('去超市买两盒牛奶');
    expect(res.body.created_at).toEqual(expect.any(String));
  });

  test('returns 400 when name is missing', async () => {
    const res = await request(app)
      .post('/todos')
      .send({ description: 'desc' })
      .expect(400);
    expect(res.body.error).toBe('name is required');
  });

  test('returns 400 when description is missing', async () => {
    const res = await request(app)
      .post('/todos')
      .send({ name: 'task' })
      .expect(400);
    expect(res.body.error).toBe('description is required');
  });

  test('trims whitespace around name and description', async () => {
    const res = await request(app)
      .post('/todos')
      .send({ name: '  task  ', description: '  desc  ' })
      .expect(201);
    expect(res.body.name).toBe('task');
    expect(res.body.description).toBe('desc');
  });
});

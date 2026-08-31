const { createDb } = require('../src/db');

describe('createDb', () => {
  test('creates the todos table with expected columns', () => {
    const db = createDb(':memory:');
    const row = db
      .prepare("SELECT name FROM sqlite_master WHERE type='table' AND name='todos'")
      .get();
    expect(row).toBeDefined();
    expect(row.name).toBe('todos');

    const cols = db.prepare('PRAGMA table_info(todos)').all();
    const names = cols.map((c) => c.name);
    expect(names).toEqual(expect.arrayContaining(['id', 'name', 'description', 'created_at']));
    db.close();
  });

  test('is idempotent (does not error on second call)', () => {
    const db = createDb(':memory:');
    expect(() => createDb(':memory:')).not.toThrow();
    db.close();
  });
});
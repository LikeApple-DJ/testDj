import Database from 'better-sqlite3';
import { Todo, CreateTodoInput } from '../models/todo';

interface TodoRow {
  id: number;
  name: string;
  description: string | null;
  created_at: string;
}

function isTodoRow(value: unknown): value is TodoRow {
  if (value === null || value === undefined || typeof value !== 'object') {
    return false;
  }
  const row = value as Record<string, unknown>;
  return (
    typeof row.id === 'number' &&
    typeof row.name === 'string' &&
    (typeof row.description === 'string' || row.description === null) &&
    typeof row.created_at === 'string'
  );
}

export class TodoRepository {
  private db: Database.Database;

  constructor(dbPath: string) {
    this.db = new Database(dbPath);
    this.db.pragma('journal_mode = WAL');
    this.db.exec(`
      CREATE TABLE IF NOT EXISTS todos (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        description TEXT,
        created_at TEXT NOT NULL DEFAULT (datetime('now'))
      )
    `);
  }

  create(input: CreateTodoInput): Todo {
    const stmt = this.db.prepare(
      'INSERT INTO todos (name, description) VALUES (?, ?)'
    );
    const result = stmt.run(input.name, input.description ?? null);
    const row = this.db
      .prepare('SELECT id, name, description, created_at FROM todos WHERE id = ?')
      .get(result.lastInsertRowid);

    if (!isTodoRow(row)) {
      throw new Error(`Todo not found after insert (id=${result.lastInsertRowid})`);
    }

    return {
      id: row.id,
      name: row.name,
      description: row.description,
      createdAt: row.created_at,
    };
  }
}
import Database from 'better-sqlite3';
import { Todo, CreateTodoInput } from '../models/todo';

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
      .get(result.lastInsertRowid) as any;

    return {
      id: row.id,
      name: row.name,
      description: row.description,
      createdAt: row.created_at,
    };
  }
}
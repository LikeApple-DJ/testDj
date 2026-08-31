const Database = require('better-sqlite3');
const fs = require('fs');
const { dirname } = require('path');

const DB_PATH = process.env.DB_PATH || './data/todos.db';

function createDb(path = DB_PATH) {
  if (path !== ':memory:') {
    fs.mkdirSync(dirname(path), { recursive: true });
  }
  const db = new Database(path);
  db.pragma('journal_mode = WAL');
  db.exec(`
    CREATE TABLE IF NOT EXISTS todos (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      description TEXT NOT NULL,
      created_at TEXT NOT NULL DEFAULT (datetime('now'))
    )
  `);
  return db;
}

module.exports = { createDb, DB_PATH };

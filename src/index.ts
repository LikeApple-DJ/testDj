import { createApp } from './app';
import { TodoRepository } from './storage/todo-repository';
import * as fs from 'fs';
import * as path from 'path';

const PORT = parseInt(process.env.PORT || '3000', 10);
const DB_PATH = process.env.DB_PATH || path.join(__dirname, '..', 'data', 'todo.db');

const dir = path.dirname(DB_PATH);
if (!fs.existsSync(dir)) {
  fs.mkdirSync(dir, { recursive: true });
}

const repo = new TodoRepository(DB_PATH);
const app = createApp(repo);

app.listen(PORT, () => {
  console.log(`Todo API listening on http://localhost:${PORT}`);
});
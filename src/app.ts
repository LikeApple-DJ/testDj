import express from 'express';
import { TodoRepository } from './storage/todo-repository';
import { createTodoRoutes } from './routes/todo-routes';

export function createApp(repo: TodoRepository): express.Express {
  const app = express();
  app.use(express.json());
  app.use('/api/todos', createTodoRoutes(repo));
  return app;
}
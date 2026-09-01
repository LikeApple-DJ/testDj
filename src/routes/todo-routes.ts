import { Router, Request, Response } from 'express';
import { TodoRepository } from '../storage/todo-repository';

export function createTodoRoutes(repo: TodoRepository): Router {
  const router = Router();

  router.post('/', (req: Request, res: Response) => {
    const { name, description } = req.body;

    if (typeof name !== 'string' || name.trim().length === 0) {
      res.status(400).json({ error: 'name is required and must be a non-empty string' });
      return;
    }
    if (name.trim().length > 200) {
      res.status(400).json({ error: 'name must not exceed 200 characters' });
      return;
    }
    if (description !== undefined && description !== null) {
      if (typeof description !== 'string') {
        res.status(400).json({ error: 'description must be a string' });
        return;
      }
      if (description.length > 2000) {
        res.status(400).json({ error: 'description must not exceed 2000 characters' });
        return;
      }
    }

    const todo = repo.create({ name: name.trim(), description: description ?? undefined });
    res.status(201).json(todo);
  });

  return router;
}
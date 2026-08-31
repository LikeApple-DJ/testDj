const express = require('express');

function createTodoRouter(db) {
  const router = express.Router();

  router.post('/', (req, res) => {
    const body = req.body || {};
    const name = typeof body.name === 'string' ? body.name.trim() : '';
    const description = typeof body.description === 'string' ? body.description.trim() : '';

    if (name === '') {
      return res.status(400).json({ error: 'name is required' });
    }
    if (description === '') {
      return res.status(400).json({ error: 'description is required' });
    }

    const insert = db.prepare(
      'INSERT INTO todos (name, description) VALUES (?, ?)'
    );
    const info = insert.run(name, description);

    const todo = db.prepare('SELECT * FROM todos WHERE id = ?').get(info.lastInsertRowid);
    return res.status(201).json(todo);
  });

  return router;
}

module.exports = { createTodoRouter };

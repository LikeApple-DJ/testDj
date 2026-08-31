const express = require('express');
const { createTodoRouter } = require('./routes/todos');

function createApp(db) {
  const app = express();
  app.use(express.json());
  app.use('/todos', createTodoRouter(db));
  return app;
}

module.exports = { createApp };

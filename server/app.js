import express from 'express';
import cors from 'cors';

import helloworldRouter from './routes/helloworld.js';
import hashRouter from './routes/hash.js';
import bubbleSortRouter from './routes/bubbleSort.js';
import exportRouter from './routes/export.js';

const app = express();
const PORT = process.env.PORT || 3001;
const CORS_ORIGIN = process.env.CORS_ORIGIN || 'http://localhost:5173';

app.use(cors({ origin: CORS_ORIGIN }));
app.use(express.json());

// Health check
app.get('/api/v1/health', (req, res) => {
  res.json({ code: 0, message: 'ok', data: null });
});

// Mount route modules
app.use('/api/v1/helloworld', helloworldRouter);
app.use('/api/v1/hash', hashRouter);
app.use('/api/v1/bubble-sort', bubbleSortRouter);
app.use('/api/v1/export', exportRouter);

// Global error handler
app.use((err, req, res, next) => {
  console.error('Unhandled error:', err);
  res.status(500).json({
    code: 'DEMO_999',
    message: 'Internal Server Error',
    data: null
  });
});

app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});
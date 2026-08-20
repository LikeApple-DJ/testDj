import { Router } from 'express';

const router = Router();

router.post('/', (req, res) => {
  const { name = 'World' } = req.body || {};

  if (name !== undefined && typeof name !== 'string') {
    return res.status(400).json({
      code: 'DEMO_001',
      message: 'name must be a string',
      data: null
    });
  }

  res.json({
    code: 0,
    message: 'ok',
    data: { greeting: `Hello, ${name}!` }
  });
});

export default router;
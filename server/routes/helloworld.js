import { Router } from 'express';

const router = Router();

router.post('/', (req, res) => {
  const { name = 'World' } = req.body || {};
  res.json({
    code: 0,
    message: 'ok',
    data: { greeting: `Hello, ${name}!` }
  });
});

export default router;
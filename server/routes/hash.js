import { Router } from 'express';
import crypto from 'crypto';

const router = Router();
const SUPPORTED = ['MD5', 'SHA-256'];

router.post('/', (req, res) => {
  const { text = '', algorithm = 'SHA-256' } = req.body || {};

  if (!SUPPORTED.includes(algorithm)) {
    return res.status(400).json({
      code: 400,
      message: `Unsupported algorithm. Use one of: ${SUPPORTED.join(', ')}`,
      data: null
    });
  }

  const hash = crypto.createHash(algorithm.toLowerCase()).update(text).digest('hex');

  res.json({
    code: 0,
    message: 'ok',
    data: { input: text, algorithm, hash }
  });
});

export default router;
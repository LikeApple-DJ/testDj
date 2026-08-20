import { Router } from 'express';
import crypto from 'crypto';

const router = Router();
const SUPPORTED = ['MD5', 'SHA-256'];

router.post('/', (req, res) => {
  const { text = '', algorithm = 'SHA-256' } = req.body || {};

  if (typeof text !== 'string' || text.length === 0) {
    return res.status(400).json({
      code: 'DEMO_002',
      message: 'text is required and must be a non-empty string',
      data: null
    });
  }

  const algorithmUpper = typeof algorithm === 'string' ? algorithm.toUpperCase() : algorithm;

  if (!SUPPORTED.includes(algorithmUpper)) {
    return res.status(400).json({
      code: 'DEMO_003',
      message: `Unsupported algorithm. Use one of: ${SUPPORTED.join(', ')}`,
      data: null
    });
  }

  const hash = crypto.createHash(algorithmUpper.toLowerCase()).update(text).digest('hex');

  res.json({
    code: 0,
    message: 'ok',
    data: { input: text, algorithm: algorithmUpper, hash }
  });
});

export default router;
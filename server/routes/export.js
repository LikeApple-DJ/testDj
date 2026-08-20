import { Router } from 'express';

const router = Router();
const SUPPORTED_TYPES = ['helloworld', 'hash', 'bubble-sort'];
const SUPPORTED_FORMATS = ['txt', 'json'];

router.post('/', (req, res) => {
  const { type = 'helloworld', data = {}, format = 'txt' } = req.body || {};

  if (!SUPPORTED_TYPES.includes(type)) {
    return res.status(400).json({
      code: 'DEMO_006',
      message: 'Unsupported export type',
      data: null
    });
  }

  if (!SUPPORTED_FORMATS.includes(format)) {
    return res.status(400).json({
      code: 'DEMO_007',
      message: 'Unsupported export format. Use one of: txt, json',
      data: null
    });
  }

  let content = '';
  let extension = 'txt';
  let contentType = 'text/plain';

  if (format === 'json') {
    extension = 'json';
    contentType = 'application/json';
    content = JSON.stringify({ type, ...data }, null, 2);
  } else {
    extension = 'txt';
    contentType = 'text/plain';
    content = `Type: ${type}\n`;
    for (const [key, value] of Object.entries(data)) {
      content += `${key}: ${JSON.stringify(value)}\n`;
    }
  }

  const filename = `result-${type}.${extension}`;
  res.setHeader('Content-Disposition', `attachment; filename="${filename}"`);
  res.setHeader('Content-Type', contentType);
  res.send(content);
});

export default router;
import { Router } from 'express';

const router = Router();

router.post('/', (req, res) => {
  const { type = 'helloworld', data = {}, format = 'txt' } = req.body || {};

  if (!['helloworld', 'hash', 'bubble-sort'].includes(type)) {
    return res.status(400).json({
      code: 400,
      message: 'Unsupported export type',
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
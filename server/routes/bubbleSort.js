import { Router } from 'express';

const router = Router();
const MAX_ARRAY_LENGTH = 5000;

function bubbleSort(arr) {
  const list = [...arr];
  let steps = 0;
  for (let i = 0; i < list.length - 1; i++) {
    for (let j = 0; j < list.length - 1 - i; j++) {
      steps++;
      if (list[j] > list[j + 1]) {
        [list[j], list[j + 1]] = [list[j + 1], list[j]];
      }
    }
  }
  return { output: list, steps };
}

router.post('/', (req, res) => {
  const { array = [] } = req.body || {};

  if (!Array.isArray(array) || array.length === 0) {
    return res.status(400).json({
      code: 'DEMO_004',
      message: 'array must be a non-empty array',
      data: null
    });
  }

  if (array.length > MAX_ARRAY_LENGTH) {
    return res.status(400).json({
      code: 'DEMO_010',
      message: `array length exceeds maximum of ${MAX_ARRAY_LENGTH}`,
      data: null
    });
  }

  if (!array.every((n) => Number.isInteger(n))) {
    return res.status(400).json({
      code: 'DEMO_005',
      message: 'array elements must be integers',
      data: null
    });
  }

  const { output, steps } = bubbleSort(array);

  res.json({
    code: 0,
    message: 'ok',
    data: { input: array, output, steps }
  });
});

export default router;
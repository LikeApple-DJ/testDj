import { Router } from 'express';

const router = Router();

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

  if (!Array.isArray(array) || !array.every((n) => typeof n === 'number')) {
    return res.status(400).json({
      code: 400,
      message: 'array must be an array of numbers',
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
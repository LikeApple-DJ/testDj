# REVIEW.md — Project Review Profile

> Canonical review profile for this repository. Maintained by the code-review skill.

## Project shape

- **Language/runtime:** Python 3.12, standard library only (`dataclasses`, `datetime`,
  `json`, `pathlib`, `argparse`, `unittest`, `tempfile`). **Zero third-party deps.**
- **App:** `todoapp` — minimal CLI todo app. Minimal closed loop = **create only**
  (no list/update/delete; YAGNI enforced by plan).
- **Layers:** `model.py` (dataclass) → `storage.py` (JSONL persistence) → `cli.py`
  (argparse `add` subcommand) → `__main__.py` (`python -m todoapp`).
- **Storage:** one compact JSON object per line appended to `todos.jsonl` (JSONL),
  UTF-8, `ensure_ascii=False`, parent dir auto-created.
- **Tests:** `python -m unittest discover`. Real file IO via `tempfile`, **no mocks**
  for the system under test. CLI exercised both in-process and via real subprocess.

## Project-specific review gates

1. **Zero third-party deps** — any `import` of a non-stdlib package is a blocker.
2. **Create-only closed loop (YAGNI)** — no list/update/delete CLI surface or storage
   helpers beyond what the create path needs. `load_todos` is allowed only as the
   verification helper called out by the plan.
3. **Real behavior tests** — tests must exercise real file IO and the real
   `python -m todoapp` entry; mocking the system under test is a blocker.
4. **Field contract** — `Todo` has exactly `name` (stripped non-empty or
   `ValueError`), `description` (may be empty), `created_at` (UTC ISO 8601,
   second precision, system-generated).
5. **Persistence contract** — JSONL append-only; parent dir auto-created; file
   missing → `[]`; compact JSON (`separators=(",", ":")`).
6. **CLI contract** — `main(argv) -> int`; success stdout must contain the todo
   name **and** the storage location; illegal name → non-zero exit, stderr message,
   no file written.
7. **Environment** — git is read-only in this workspace; untracked new files are the
   norm, not an error. Do not block on absence of commits.

## Output / evidence expectations

- Test evidence must show the real `unittest` output, including the real-subprocess
  CLI test covering `__main__.py`.
- Smoke artifacts (`todos.jsonl` from manual runs) must be cleaned up and must not be
  delivered as part of the change.

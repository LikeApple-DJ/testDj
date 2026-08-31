# REVIEW.md — Project Review Profile

> Profile for the Flask 待办事项 (todo create) application. Used by the code-review skill.

## Project facts

- **Stack:** Python 3 + Flask 3 + Jinja2 (server-side rendered), pytest.
- **Scope:** Minimal closed loop — create todo only. No edit/delete/auth/database/pagination/frontend framework (YAGNI enforced by spec §9).
- **Entry:** `app.py` (single-file Flask app), `templates/index.html` (form + list).
- **Storage:** Local `todos.json` (JSON array); missing/corrupt → empty list; runtime file, must NOT be committed.
- **Data model:** `{id, name, description, created_at}`; `id` auto-increment from 1; `created_at` ISO 8601 UTC server-side.
- **Routes:** `GET /` (render form + list), `POST /todos` (validate → generate → append → write → redirect).
- **Tests:** `tests/test_app.py` (integration), `tests/test_data.py` (data-layer unit). Run via `.venv/bin/pytest` or `pytest`.

## Project-specific review gates

- **G1 — Spec/plan/code alignment:** `app.py`, template, and tests must match `docs/superpowers/specs/2026-08-31-todo-create-design.md` and `docs/superpowers/plans/2026-08-31-todo-create.md` (fields, validation, id, created_at, reverse list, flash).
- **G2 — Runtime data hygiene:** `todos.json` is runtime-only; repo must have `.gitignore` and no `.venv`/`__pycache__` tracked.
- **G3 — YAGNI boundary:** No features beyond create + list feedback (no edit/delete/auth/DB/pagination).
- **G4 — Test strength:** Tests assert behavior (persisted records, redirect body, id sequence), not just status codes or mocks.

## Profile state notes

- Created during review (no prior `REVIEW.md` existed): `CREATED_AND_USED`.

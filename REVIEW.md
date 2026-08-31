# REVIEW.md — Project Review Profile

> Profile for the `todo-app` repository. Maintained by the code-review skill.

## Project Snapshot

- **Stack:** Node.js (>=18 LTS), Express ^4.19.2, better-sqlite3 ^11.0.0, Jest ^29.7.0, supertest ^6.3.4.
- **Scope:** Minimal backend for internal users to **create** daily todo items (create-only closed loop; no list/edit/delete).
- **Entry point:** `src/server.js` → builds `db` + `app`, listens on `PORT` (default 3000).
- **Test command:** `npm test` (jest --runInBand).

## Architecture & Interfaces

| Module | Exports | Responsibility |
|--------|---------|----------------|
| `src/db.js` | `createDb(path?)`, `DB_PATH` | SQLite connection factory + `todos` schema (CREATE TABLE IF NOT EXISTS). `path` defaults to `process.env.DB_PATH \|\| './data/todos.db'`; `:memory:` for tests. |
| `src/app.js` | `createApp(db)` | Express app factory; mounts `express.json()` + `/todos` router. `db` injected for testability. |
| `src/routes/todos.js` | `createTodoRouter(db)` | `POST /` route: validates + trims `name`/`description`, inserts, returns 201 with created row. |
| `src/server.js` | — (entry) | Creates `db` + `app`, starts HTTP listener. |

## Project-Specific Review Gates

1. **Spec fidelity:** Only create endpoint (`POST /todos`). No list/update/delete (YAGNI). Fields = `name` + `description` only, both required, non-empty, trimmed.
2. **SQL safety:** All queries must use parameterized statements (`db.prepare(...).run(...)`). No string interpolation into SQL.
3. **Test injection:** `createApp(db)` must accept an injected `db` so tests use `:memory:` SQLite — no file-system DB in tests.
4. **Validation contract:** Error messages must match exactly between route and tests (`'name is required'`, `'description is required'`). Status codes: 201 on success, 400 on validation failure.
5. **Lean surface:** No auth, no frontend, no extra endpoints. Public exports limited to the three factory functions above.

## Profile State

- **State:** CREATED_AND_USED
- **Reason:** No prior `REVIEW.md` existed; generated from project entry files (`package.json`, `src/*`, `tests/*`, plan doc) before this review.

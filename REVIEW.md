# Daily Todo — Review Profile

## Project
- **Stack**: TypeScript 5, Node.js 20, Express 4, better-sqlite3, Jest + Supertest
- **Scope**: Minimal REST API — create todo items only (no update/delete/list)
- **Audience**: Internal users (no auth)

## Review Gates

### TypeScript
- `strict: true` — no implicit any, strict null checks enforced
- No `as any` casts; prefer type guards or explicit interfaces
- All public exports must have explicit return types

### Express Routes
- Every route handler must validate input before touching storage
- Error responses: `{ error: "<message>" }` with appropriate HTTP status
- Request body: JSON only (`Content-Type: application/json`)

### SQLite / Storage
- WAL mode enabled for every database connection
- All DB writes go through `TodoRepository`; no raw SQL in route handlers
- Test databases must clean up `.db`, `.db-wal`, `.db-shm` after each test

### Testing
- Repository tests: real SQLite (no mocking)
- API tests: SuperTest against real Express app
- Every validation branch in route handlers must have a corresponding test case
- Boundary tests required for length limits (exactly at limit = pass, limit+1 = fail)

### Naming
- DB columns: `snake_case` (e.g., `created_at`)
- TypeScript interfaces: `PascalCase` (e.g., `Todo`, `CreateTodoInput`)
- Map between them at the repository boundary
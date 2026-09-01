# Daily Todo — Create Feature Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a minimal REST API that allows internal users to create daily todo items with a name and description.

**Architecture:** Single-service Node.js/Express REST API backed by SQLite. A single `POST /api/todos` endpoint accepts JSON `{ name, description }`, validates input, persists to the database, and returns the created todo with its auto-generated `id` and `createdAt` timestamp.

**Tech Stack:** TypeScript 5, Node.js 20, Express 4, better-sqlite3 (synchronous SQLite), Jest + Supertest (testing).

---

## Global Constraints

- Node.js ≥ 20 LTS; TypeScript ≥ 5.x
- Zero external runtime dependencies beyond SQLite (no PostgreSQL, Redis, or Docker)
- All source in `src/`; all tests in `tests/`
- Package manager: npm
- Database file: `data/todo.db` (auto-created on first run)
- API base path: `/api`
- Request/response bodies: JSON (`Content-Type: application/json`)
- Error responses: `{ "error": "<message>" }` with appropriate HTTP status codes
- `name` is required (1-200 chars); `description` is optional (max 2000 chars)

---

## Task 1: Project Scaffolding

**Files:**
- Create: `package.json`
- Create: `tsconfig.json`
- Create: `.gitignore`

**Interfaces:**
- Produces: `package.json` with `start`, `dev`, `test`, `build` scripts; `tsconfig.json` with `strict: true`, `outDir: dist`, `rootDir: src`

- [ ] **Step 1: Create package.json**

```json
{
  "name": "daily-todo",
  "version": "1.0.0",
  "private": true,
  "description": "Daily todo — create feature",
  "scripts": {
    "build": "tsc",
    "start": "node dist/index.js",
    "dev": "ts-node src/index.ts",
    "test": "jest --forceExit --detectOpenHandles"
  },
  "dependencies": {
    "better-sqlite3": "^11.0.0",
    "express": "^4.21.0"
  },
  "devDependencies": {
    "@types/better-sqlite3": "^7.6.0",
    "@types/express": "^4.17.0",
    "@types/jest": "^29.5.0",
    "@types/supertest": "^6.0.0",
    "jest": "^29.7.0",
    "supertest": "^7.0.0",
    "ts-jest": "^29.2.0",
    "ts-node": "^10.9.0",
    "typescript": "^5.5.0"
  }
}
```

- [ ] **Step 2: Create tsconfig.json**

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "module": "commonjs",
    "lib": ["ES2022"],
    "outDir": "dist",
    "rootDir": "src",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "resolveJsonModule": true,
    "declaration": true
  },
  "include": ["src/**/*"],
  "exclude": ["node_modules", "dist", "tests"]
}
```

- [ ] **Step 3: Create .gitignore**

```
node_modules/
dist/
data/
*.db
```

- [ ] **Step 4: Install dependencies**

Run: `npm install`
Expected: exit code 0, `node_modules/` populated.

- [ ] **Step 5: Verify TypeScript compiles (empty src)**

Run: `mkdir -p src && echo 'export {};' > src/index.ts && npx tsc --noEmit`
Expected: exit code 0, no errors.

- [ ] **Step 6: Commit**

```bash
git add package.json package-lock.json tsconfig.json .gitignore
git commit -m "chore: scaffold project with TypeScript, Express, SQLite, Jest"
```

---

## Task 2: Todo Data Model

**Files:**
- Create: `src/models/todo.ts`

**Interfaces:**
- Produces: `Todo` interface with `id: number`, `name: string`, `description: string | null`, `createdAt: string`; `CreateTodoInput` interface with `name: string`, `description?: string`

- [ ] **Step 1: Write the failing test**

Create `tests/todo.test.ts`:

```typescript
import { Todo, CreateTodoInput } from '../src/models/todo';

describe('Todo model (type-level)', () => {
  it('accepts a valid CreateTodoInput', () => {
    const input: CreateTodoInput = { name: 'Buy groceries', description: 'Milk, eggs, bread' };
    expect(input.name).toBe('Buy groceries');
    expect(input.description).toBe('Milk, eggs, bread');
  });

  it('accepts CreateTodoInput without description', () => {
    const input: CreateTodoInput = { name: 'Walk the dog' };
    expect(input.name).toBe('Walk the dog');
    expect(input.description).toBeUndefined();
  });

  it('has correct Todo shape', () => {
    const todo: Todo = {
      id: 1,
      name: 'Test',
      description: null,
      createdAt: '2026-09-01T00:00:00.000Z',
    };
    expect(todo.id).toBe(1);
    expect(todo.createdAt).toBe('2026-09-01T00:00:00.000Z');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npx jest tests/todo.test.ts`
Expected: FAIL — module `../src/models/todo` not found.

- [ ] **Step 3: Create the model**

Create `src/models/todo.ts`:

```typescript
export interface CreateTodoInput {
  name: string;
  description?: string;
}

export interface Todo {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npx jest tests/todo.test.ts`
Expected: PASS — 3 tests pass.

- [ ] **Step 5: Commit**

```bash
git add src/models/todo.ts tests/todo.test.ts
git commit -m "feat: add Todo and CreateTodoInput data models"
```

---

## Task 3: SQLite Storage Layer

**Files:**
- Create: `src/storage/todo-repository.ts`
- Modify: `tests/todo.test.ts` (add repository tests)

**Interfaces:**
- Consumes: `Todo`, `CreateTodoInput` from `src/models/todo`
- Produces: `TodoRepository` class with `constructor(dbPath: string)` and `create(input: CreateTodoInput): Todo`

- [ ] **Step 1: Write the failing repository test**

Append to `tests/todo.test.ts`:

```typescript
import Database from 'better-sqlite3';
import { TodoRepository } from '../src/storage/todo-repository';
import * as fs from 'fs';
import * as path from 'path';

describe('TodoRepository', () => {
  const testDbPath = path.join(__dirname, '..', 'data', 'test-todo.db');
  let repo: TodoRepository;

  beforeEach(() => {
    const dir = path.dirname(testDbPath);
    if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
    if (fs.existsSync(testDbPath)) fs.unlinkSync(testDbPath);
    repo = new TodoRepository(testDbPath);
  });

  afterEach(() => {
    if (fs.existsSync(testDbPath)) fs.unlinkSync(testDbPath);
  });

  it('creates a todo and returns it with id and createdAt', () => {
    const result = repo.create({ name: 'Buy groceries', description: 'Milk, eggs' });
    expect(result.id).toBe(1);
    expect(result.name).toBe('Buy groceries');
    expect(result.description).toBe('Milk, eggs');
    expect(result.createdAt).toEqual(expect.any(String));
    expect(new Date(result.createdAt).getTime()).not.toBeNaN();
  });

  it('creates a todo with null description when omitted', () => {
    const result = repo.create({ name: 'Walk the dog' });
    expect(result.description).toBeNull();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npx jest tests/todo.test.ts -t "TodoRepository"`
Expected: FAIL — module `../src/storage/todo-repository` not found.

- [ ] **Step 3: Implement TodoRepository**

Create `src/storage/todo-repository.ts`:

```typescript
import Database from 'better-sqlite3';
import { Todo, CreateTodoInput } from '../models/todo';

export class TodoRepository {
  private db: Database.Database;

  constructor(dbPath: string) {
    this.db = new Database(dbPath);
    this.db.pragma('journal_mode = WAL');
    this.db.exec(`
      CREATE TABLE IF NOT EXISTS todos (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        description TEXT,
        created_at TEXT NOT NULL DEFAULT (datetime('now'))
      )
    `);
  }

  create(input: CreateTodoInput): Todo {
    const stmt = this.db.prepare(
      'INSERT INTO todos (name, description) VALUES (?, ?)'
    );
    const result = stmt.run(input.name, input.description ?? null);
    const row = this.db
      .prepare('SELECT id, name, description, created_at FROM todos WHERE id = ?')
      .get(result.lastInsertRowid) as any;

    return {
      id: row.id,
      name: row.name,
      description: row.description,
      createdAt: row.created_at,
    };
  }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `npx jest tests/todo.test.ts -t "TodoRepository"`
Expected: PASS — 2 tests pass.

- [ ] **Step 5: Commit**

```bash
git add src/storage/todo-repository.ts tests/todo.test.ts
git commit -m "feat: add SQLite TodoRepository with create method"
```

---

## Task 4: Express App + POST /api/todos Route

**Files:**
- Create: `src/routes/todo-routes.ts`
- Create: `src/app.ts`
- Modify: `tests/todo.test.ts` (add API integration tests)

**Interfaces:**
- Consumes: `TodoRepository` from `src/storage/todo-repository`
- Produces: `createApp(repo: TodoRepository): Express` from `src/app.ts`; route `POST /api/todos` with request body `{ name: string, description?: string }` and response `201 { id, name, description, createdAt }`

- [ ] **Step 1: Write the failing API integration test**

Append to `tests/todo.test.ts`:

```typescript
import request from 'supertest';
import { createApp } from '../src/app';

describe('POST /api/todos', () => {
  const testDbPath = path.join(__dirname, '..', 'data', 'test-api-todo.db');
  let app: ReturnType<typeof createApp>;

  beforeEach(() => {
    const dir = path.dirname(testDbPath);
    if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
    if (fs.existsSync(testDbPath)) fs.unlinkSync(testDbPath);
    const repo = new TodoRepository(testDbPath);
    app = createApp(repo);
  });

  afterEach(() => {
    if (fs.existsSync(testDbPath)) fs.unlinkSync(testDbPath);
  });

  it('returns 201 with the created todo on valid input', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ name: 'Buy groceries', description: 'Milk, eggs' })
      .expect('Content-Type', /json/)
      .expect(201);

    expect(res.body).toMatchObject({
      id: 1,
      name: 'Buy groceries',
      description: 'Milk, eggs',
    });
    expect(res.body.createdAt).toEqual(expect.any(String));
  });

  it('returns 201 with null description when omitted', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ name: 'Walk the dog' })
      .expect(201);

    expect(res.body.description).toBeNull();
  });

  it('returns 400 when name is missing', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ description: 'No name' })
      .expect(400);

    expect(res.body.error).toContain('name');
  });

  it('returns 400 when name is empty', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ name: '' })
      .expect(400);

    expect(res.body.error).toContain('name');
  });

  it('returns 400 when name exceeds 200 characters', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ name: 'x'.repeat(201) })
      .expect(400);

    expect(res.body.error).toContain('name');
  });

  it('returns 400 when description exceeds 2000 characters', async () => {
    const res = await request(app)
      .post('/api/todos')
      .send({ name: 'Valid', description: 'x'.repeat(2001) })
      .expect(400);

    expect(res.body.error).toContain('description');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `npx jest tests/todo.test.ts -t "POST /api/todos"`
Expected: FAIL — module `../src/app` not found.

- [ ] **Step 3: Create the route handler**

Create `src/routes/todo-routes.ts`:

```typescript
import { Router, Request, Response } from 'express';
import { TodoRepository } from '../storage/todo-repository';

export function createTodoRoutes(repo: TodoRepository): Router {
  const router = Router();

  router.post('/', (req: Request, res: Response) => {
    const { name, description } = req.body;

    if (typeof name !== 'string' || name.trim().length === 0) {
      res.status(400).json({ error: 'name is required and must be a non-empty string' });
      return;
    }
    if (name.length > 200) {
      res.status(400).json({ error: 'name must not exceed 200 characters' });
      return;
    }
    if (description !== undefined && description !== null) {
      if (typeof description !== 'string') {
        res.status(400).json({ error: 'description must be a string' });
        return;
      }
      if (description.length > 2000) {
        res.status(400).json({ error: 'description must not exceed 2000 characters' });
        return;
      }
    }

    const todo = repo.create({ name: name.trim(), description: description ?? undefined });
    res.status(201).json(todo);
  });

  return router;
}
```

- [ ] **Step 4: Create the Express app**

Create `src/app.ts`:

```typescript
import express from 'express';
import { TodoRepository } from './storage/todo-repository';
import { createTodoRoutes } from './routes/todo-routes';

export function createApp(repo: TodoRepository): express.Express {
  const app = express();
  app.use(express.json());
  app.use('/api/todos', createTodoRoutes(repo));
  return app;
}
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `npx jest tests/todo.test.ts`
Expected: PASS — all 11 tests pass (3 model + 2 repository + 6 API).

- [ ] **Step 6: Commit**

```bash
git add src/routes/todo-routes.ts src/app.ts tests/todo.test.ts
git commit -m "feat: add Express app with POST /api/todos route and validation"
```

---

## Task 5: Entry Point

**Files:**
- Create: `src/index.ts`

**Interfaces:**
- Consumes: `createApp` from `src/app.ts`, `TodoRepository` from `src/storage/todo-repository`
- Produces: Runnable server on port from `PORT` env var (default 3000)

- [ ] **Step 1: Create the entry point**

Create `src/index.ts`:

```typescript
import { createApp } from './app';
import { TodoRepository } from './storage/todo-repository';
import * as fs from 'fs';
import * as path from 'path';

const PORT = parseInt(process.env.PORT || '3000', 10);
const DB_PATH = process.env.DB_PATH || path.join(__dirname, '..', 'data', 'todo.db');

const dir = path.dirname(DB_PATH);
if (!fs.existsSync(dir)) {
  fs.mkdirSync(dir, { recursive: true });
}

const repo = new TodoRepository(DB_PATH);
const app = createApp(repo);

app.listen(PORT, () => {
  console.log(`Todo API listening on http://localhost:${PORT}`);
});
```

- [ ] **Step 2: Build and verify**

Run: `npx tsc`
Expected: exit code 0, `dist/` directory populated with compiled JS.

- [ ] **Step 3: Smoke test the server**

Run (background): `node dist/index.js &`
Run: `sleep 1 && curl -s -X POST http://localhost:3000/api/todos -H 'Content-Type: application/json' -d '{"name":"Smoke test","description":"Verify server works"}'`
Expected: `{"id":1,"name":"Smoke test","description":"Verify server works","createdAt":"..."}`
Run: `kill %1`

- [ ] **Step 4: Run full test suite**

Run: `npx jest`
Expected: PASS — all 11 tests pass.

- [ ] **Step 5: Commit**

```bash
git add src/index.ts
git commit -m "feat: add server entry point with configurable port and DB path"
```

---

## Self-Review

### 1. Spec coverage
- ✅ "新增待办事项" (create todo) → Task 4: POST /api/todos route
- ✅ "事项名称" (name) → Task 2: Todo model `name` field; Task 4: validation
- ✅ "描述" (description) → Task 2: Todo model `description` field; Task 4: validation
- ✅ "内部用户" (internal users) → No auth required per scope; simple REST API
- ✅ "最小闭环：仅创建" (minimal closed loop: only create) → Only POST endpoint, no GET/UPDATE/DELETE

### 2. Placeholder scan
- No TBD, TODO, or placeholder language found.

### 3. Type consistency
- `CreateTodoInput` defined in Task 2, consumed by Task 3 (`TodoRepository.create`) and Task 4 (`POST /api/todos`).
- `Todo` returned by Task 3, passed through to Task 4 response.
- `TodoRepository` instantiated in Task 3, injected into `createApp` in Task 4, used in Task 5 entry point.
- `createApp` signature consistent across Tasks 4 and 5.
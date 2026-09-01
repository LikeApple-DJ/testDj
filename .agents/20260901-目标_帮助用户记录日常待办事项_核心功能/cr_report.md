# Code Review Report — Daily Todo Create Feature

**Review Date**: 2026-09-01  
**Review Target**: Implementation Plan → Actual Code (7 files, 288 lines)  
**Plan**: `docs/superpowers/plans/2026-09-01-daily-todo-create.md`  
**Requirement**: 帮助用户记录日常待办事项，核心功能：新增待办事项（名称+描述），最小闭环：仅创建

---

## Project Profile

**State**: CREATED_AND_USED  
**Source**: `REVIEW.md` (generated from project context)  
**Notes**: No prior `REVIEW.md` existed. Created from `package.json`, `tsconfig.json`, and source code conventions. Gates cover TypeScript strictness, Express route validation, SQLite WAL/test cleanup, and test boundary coverage.

---

## Lane Verdict Table

| Lane | Verdict | Notes |
|---|---|---|
| Align | APPROVE_WITH_COMMENTS | Plan-to-code drift on trim() validation and type guard; code is correct but plan is stale |
| Design | APPROVE | Clean separation: model → repository → routes → app; DI pattern for testability |
| Trim | APPROVE_WITH_COMMENTS | One unused import in test file; otherwise minimal surface |
| Cause | NOT_RUN | New feature, not a bug fix; no root-cause claim to verify |
| Verify | APPROVE_WITH_COMMENTS | 11 tests cover happy path + validation errors; gaps in boundary/type/whitespace tests |

---

## Blocking Findings

**No CRITICAL or HIGH findings.** Blocker count: **0**

---

## Advisory Findings

### [WARNING] [ALIGN] [CLAIM-DRIFT] docs/superpowers/plans/2026-09-01-daily-todo-create.md:430 — plan uses `name.length` but code uses `name.trim().length`
**Evidence**: Plan line 430 shows `if (name.length > 200)`. Actual code at `src/routes/todo-routes.ts:14` uses `name.trim().length > 200`. The code is an improvement (whitespace-only names are caught earlier, and stored names are trimmed), but the plan was not updated to reflect the change.
**Recommendation**: Update the plan to document the `name.trim()` behavior for both validation and storage.

### [WARNING] [ALIGN] [CLAIM-DRIFT] docs/superpowers/plans/2026-09-01-daily-todo-create.md:284 — plan uses `as any` cast but code uses `isTodoRow` type guard
**Evidence**: Plan line 284 uses `get(result.lastInsertRowid) as any`. Actual code at `src/storage/todo-repository.ts:45-51` uses a proper `isTodoRow` type guard with `TodoRow` interface. The code is safer, but the plan is stale.
**Recommendation**: Update the plan to reflect the type-safe implementation.

### [WARNING] [ALIGN] [CLAIM-DRIFT] docs/superpowers/plans/2026-09-01-daily-todo-create.md:228-229 — plan afterEach only deletes `.db` file; code also cleans up `.db-wal` and `.db-shm`
**Evidence**: Plan lines 228-229 only show `fs.unlinkSync(testDbPath)`. Actual code at `tests/todo.test.ts:44-48` and `tests/todo.test.ts:81-85` also cleans up WAL (`-wal`) and shared memory (`-shm`) companion files. This prevents test pollution from WAL mode.
**Recommendation**: Update the plan to document WAL file cleanup.

### [WARNING] [TRIM] [DEAD-CODE] tests/todo.test.ts:28 — unused import `Database`
**Evidence**: `import Database from 'better-sqlite3';` is imported at line 28 but never referenced in the test file. `TodoRepository` internally uses `Database`, so the test doesn't need it.
**Recommendation**: Remove the unused import.

### [WARNING] [VERIFY] [TEST-GAP] tests/todo.test.ts — missing test for whitespace-only name
**Evidence**: Route handler at `src/routes/todo-routes.ts:10` rejects `name.trim().length === 0`. Test at line 121 only covers `{ name: '' }`, not `{ name: '   ' }`. A whitespace-only name should also return 400.
**Recommendation**: Add test: `it('returns 400 when name is whitespace only', ...)` with `{ name: '   ' }`.

### [WARNING] [VERIFY] [TEST-GAP] tests/todo.test.ts — missing boundary tests
**Evidence**: 
- No test for name exactly 200 chars (should pass, return 201)
- No test for description exactly 2000 chars (should pass, return 201)
**Recommendation**: Add boundary tests for the upper limits.

### [WARNING] [VERIFY] [TEST-GAP] tests/todo.test.ts — missing type validation tests
**Evidence**: Route handler at `src/routes/todo-routes.ts:10` checks `typeof name !== 'string'` and line 19 checks `typeof description !== 'string'`. No tests cover sending non-string values (e.g., `{ name: 123 }` or `{ name: 'Valid', description: 123 }`).
**Recommendation**: Add tests for non-string name and non-string description inputs.

### [INFO] [VERIFY] [ERROR-PATH] src/index.ts:14 — no error handling for `app.listen` failure
**Evidence**: If the port is already in use, `app.listen` emits an 'error' event and the process crashes with an unhandled error. This is acceptable for the minimal scope but worth noting.
**Recommendation**: Consider adding `server.on('error', ...)` or a process-level `uncaughtException` handler for production readiness.

### [INFO] [VERIFY] [BOUNDARY-CASE] src/storage/todo-repository.ts:17 — `isTodoRow` checks `typeof row.id === 'number'` but `lastInsertRowid` can be `bigint`
**Evidence**: better-sqlite3's `RunResult.lastInsertRowid` is typed as `number | bigint`. If a bigint is returned, `typeof bigint !== 'number'` and the guard would reject it. In practice, SQLite AUTOINCREMENT IDs stay within safe integer range, so this is unlikely to trigger.
**Recommendation**: No action needed for current scope; note for future if large-scale usage is expected.

---

## Skipped Lanes and Reasons

| Lane | Reason |
|---|---|
| Cause | New feature implementation, not a bug fix. No root-cause claim to verify. |

---

## Suggested Next Actions

1. **Update the plan** (`docs/superpowers/plans/2026-09-01-daily-todo-create.md`) to reflect actual code: `name.trim()` validation, `isTodoRow` type guard, WAL file cleanup.
2. **Remove unused import** `Database` from `tests/todo.test.ts:28`.
3. **Add missing test cases**: whitespace-only name, boundary values (200-char name, 2000-char description), non-string type inputs.
4. **Consider adding** `description: null` test case for explicit null in JSON body.

---

## Test Coverage Summary

| Category | Tests | Status |
|---|---|---|
| Model (type-level) | 3 | ✓ PASS |
| Repository (create) | 2 | ✓ PASS |
| API (happy path) | 2 | ✓ PASS |
| API (validation errors) | 4 | ✓ PASS |
| **Missing: whitespace** | 0 | ✗ GAP |
| **Missing: boundary** | 0 | ✗ GAP |
| **Missing: type validation** | 0 | ✗ GAP |

---

**VERDICT: APPROVE_WITH_COMMENTS**

No blocking issues. The implementation correctly fulfills the requirement: a minimal REST API for creating daily todo items with name and description. Code quality improvements (type guard, WAL cleanup, trim validation) exceed the plan. The plan document is stale and should be updated. Test coverage is adequate for the happy path and basic error cases but needs boundary and type validation additions.
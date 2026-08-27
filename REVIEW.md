# Todo App Review Profile

## Project Context
- **Stack**: React 18 (Vite) + Spring Boot 3.2 + MySQL (H2 for tests)
- **Architecture**: Monorepo (`frontend/` + `backend/`)
- **Scope**: Create + List todo items; no auth, no edit/delete
- **Target**: Internal users, development-only deployment

## Project-Specific Gates

### Backend
- Controller → Service → Repository → JPA Entity layering
- DTO validation via `jakarta.validation` (`@NotBlank`, `@Size`)
- `@ExceptionHandler(MethodArgumentNotValidException.class)` returns `{"error":"VALIDATION_ERROR","message":"..."}`
- CORS: `localhost:3000` only, methods GET/POST/OPTIONS
- Tests: `@DataJpaTest` for repository, `@ExtendWith(MockitoExtension)` for service, `@WebMvcTest` for controller
- Test config uses H2 in-memory DB

### Frontend
- Components: `App` → `TodoForm` + `TodoList` → `TodoCard`
- API client in `src/api/todoApi.js`, hardcoded `localhost:8080`
- `onCreated` callback pattern for form→parent communication
- Frontend JS validation mirrors backend: name required, maxLength=255, description maxLength=5000

### Data Contract
- `POST /api/todos` → 201 + TodoItem JSON
- `GET /api/todos` → 200 + `[{...}]` sorted by createdAt DESC
- Error: 400 + `{"error":"VALIDATION_ERROR","message":"..."}`
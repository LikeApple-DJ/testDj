# Project Review Profile

## Project: Daily Todo Application

- **Stack**: Java 17, Spring Boot 3.2.0, Spring Data JPA, H2 (in-memory), Maven, HTML5 + Vanilla JS
- **Architecture**: Monolithic — Controller → Service → Repository → H2
- **API Style**: REST with unified `ApiResponse<T>` envelope (`{result, msg, data}`)
- **Validation**: Jakarta Bean Validation (`@Valid` on controller)
- **Frontend**: Single static HTML page, zero build tools

## Project-Specific Gates

1. **API Response Contract**: All REST endpoints must return `ApiResponse<T>` wrapper. The `result` field must be `"OK"` for success, `"ERROR"` for failure. Validation errors must also conform to this contract via a global exception handler.
2. **Validation**: Server-side validation via `@Valid` + Jakarta annotations is required for all input DTOs. Frontend validation is supplementary only.
3. **Entity Lifecycle**: `createdAt` is managed by `@PrePersist` only; do not set it manually. The `updatable=false` column constraint must be respected.
4. **Configuration**: `show-sql` and `h2.console.enabled` must be `false` in production profiles.
5. **Tests**: Every public service method and controller endpoint must have at least one integration test covering the happy path and one validation failure path.
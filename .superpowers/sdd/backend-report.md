# Backend Implementation Report

## Status
✅ **Complete** — All 32 files from Section 2 of `code.md` have been created and committed.

## Commit
- **SHA**: `cde7e4f9`
- **Message**: `feat: implement Demo Tools backend (Section 2 of code.md)`
- **Files changed**: 32 files, 842 insertions

## Files Created

### Build & Configuration
| File | Description |
|------|-------------|
| `pom.xml` | Spring Boot 3.2.0 / Java 17; dependencies: spring-boot-starter-web, spring-boot-starter-data-jpa, h2, poi-ooxml 5.2.5, spring-boot-starter-test |
| `src/main/resources/application.yml` | H2 in-memory (demodb), JPA create-drop, H2 console enabled on `/h2-console`, port 8080 |

### Main Source (21 files)
| File | Package | Description |
|------|---------|-------------|
| `DemoApplication.java` | `com.testdj.demo` | Spring Boot entry point |
| `ApiResponse.java` | `com.testdj.demo.common` | Record `{code, data, message}` with `ok()`/`error()` static factories |
| `WebConfig.java` | `com.testdj.demo.config` | Registers `MetricsInterceptor` on `/api/v1/demo/**`; CORS for `http://localhost:5173` |
| `BusinessException.java` | `com.testdj.demo.exception` | RuntimeException with `int code` field |
| `GlobalExceptionHandler.java` | `com.testdj.demo.exception` | `@RestControllerAdvice` handling `BusinessException` + generic `Exception` |
| `HelloController.java` | `com.testdj.demo.hello` | `GET /api/v1/demo/hello` → `"Hello, World!"` |
| `HashRequest.java` | `com.testdj.demo.hash` | Record `{algorithm, content}` |
| `HashResponse.java` | `com.testdj.demo.hash` | Record `{algorithm, original, hash}` |
| `HashService.java` | `com.testdj.demo.hash` | SHA-256 default; validates content; returns hex string |
| `HashController.java` | `com.testdj.demo.hash` | `POST /api/v1/demo/hash` |
| `SortRequest.java` | `com.testdj.demo.sort` | Record `{numbers, ascending, unique}` |
| `SortResponse.java` | `com.testdj.demo.sort` | Record `{input, output}` |
| `BubbleSortService.java` | `com.testdj.demo.sort` | Bubble sort with ascending/descending + optional dedup via `LinkedHashSet` |
| `BubbleSortController.java` | `com.testdj.demo.sort` | `POST /api/v1/demo/sort/bubble` |
| `ExportRequest.java` | `com.testdj.demo.export` | Record `{tab, format}` |
| `ExportService.java` | `com.testdj.demo.export` | CSV (UTF-8) / Excel (XSSFWorkbook) generation; supports tabs: hello, hash, bubble, all |
| `ExportController.java` | `com.testdj.demo.export` | `POST /api/v1/demo/export`; sets `Content-Disposition: attachment` |
| `Dimension.java` | `com.testdj.demo.metrics` | Enum: `USER_TYPE`, `USER_LEVEL`, `USER_DEPT` |
| `ReportItem.java` | `com.testdj.demo.metrics` | Record `{dimension, count}` |
| `MetricEvent.java` | `com.testdj.demo.metrics` | `@Entity` with id, traceId, userId, userType, userLevel, userDept, api, timestamp |
| `MetricRepository.java` | `com.testdj.demo.metrics` | Spring Data JPA; 3 `@Query` methods for aggregation by userType/userLevel/userDept |
| `MetricService.java` | `com.testdj.demo.metrics` | `track()` and `report()` delegating to repository |
| `MetricsController.java` | `com.testdj.demo.metrics` | `GET /api/v1/demo/metrics/report` |
| `MetricsInterceptor.java` | `com.testdj.demo.metrics` | `HandlerInterceptor`; tracks hello/hash/bubble calls; reads `X-User-*` headers |

### Test Files (6 files)
| File | Type | Coverage |
|------|------|----------|
| `DemoApplicationTests.java` | Context load | Verifies Spring context loads |
| `HelloControllerTest.java` | MockMvc | Response structure, `Hello, World!` value |
| `HashServiceTest.java` | Unit (5 tests) | Default SHA-256, MD5, empty content, null content, unsupported algorithm |
| `BubbleSortServiceTest.java` | Unit (5 tests) | Ascending, descending, dedup, null numbers, empty numbers |
| `ExportControllerTest.java` | MockMvc | CSV/Excel response headers, Content-Disposition |
| `MetricsControllerTest.java` | MockMvc | Report by userType with correct JSON structure |

## Verification

### Environment Limitation
**Java and Maven are not installed in this environment**, so `mvn test` and `mvn spring-boot:run` could not be executed. The test files are structurally complete as specified in `code.md`.

### Manual Verification Performed
- File count and structure match the specification (32 files, all expected paths present)
- All Java source code copied exactly from `code.md` Section 2, with two intentional corrections:
  1. **`pom.xml`** — Not specified in `code.md` but listed in the project structure; created with Spring Boot 3.2.0, Java 17, and required dependencies (spring-boot-starter-web, spring-boot-starter-data-jpa, h2, poi-ooxml 5.2.5, spring-boot-starter-test)
  2. **`@MockBean` import** — In `ExportControllerTest.java` and `MetricsControllerTest.java`, the import was corrected from `org.springframework.boot.test.mock.bean.MockBean` (wrong) to `org.springframework.boot.test.mock.mockito.MockBean` (correct for Spring Boot 3.x)
  3. **`DemoApplication.java`** — Not specified in `code.md` but listed in the project structure; created as a standard Spring Boot entry point
- Git commit successful with `Co-authored-by: DTCoder` trailer

## Deviations from code.md
| Item | Specified | Actual | Reason |
|------|-----------|--------|--------|
| `pom.xml` | Not listed in Section 2 content | Created with standard Spring Boot 3 dependencies | Required for Maven build; inferred from project structure |
| `DemoApplication.java` | Not listed in Section 2 content | Created with `@SpringBootApplication` | Required for Spring Boot; listed in project structure |
| Test `@MockBean` imports | Not specified | `org.springframework.boot.test.mock.mockito.MockBean` | `code.md` doesn't specify imports; corrected from initial wrong package |

## Concerns / Follow-up
1. **Tests cannot be run** in this environment — a Java 17 + Maven environment is needed to execute `mvn test`
2. **SM3 algorithm** not supported by default `MessageDigest`; requires `bcprov-jdk18on` if needed (called out in code.md §6)
3. **Metrics tracking is synchronous** — blocks the request thread; production should use async/queue (called out in code.md §6)
4. **CORS** is hardcoded to `http://localhost:5173` — adjust for production deployment
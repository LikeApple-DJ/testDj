# Project Review Profile — hello-world

> Maintained by code-review skill. Short, operational, project-specific.

## Project facts
- **Type**: Java 21 Maven single-module application (`pom.xml`).
- **Coordinates**: `com.dt.example:hello-world:1.0.0-SNAPSHOT`, packaging `jar`.
- **Toolchain**: `maven-compiler-plugin` 3.12.1 (`source`/`target` 21), `maven-surefire-plugin` 3.2.5.
- **Test deps**: JUnit 5 (`junit-jupiter` 5.10.2, test scope), AssertJ (`assertj-core` 3.25.3, test scope).
- **Entry point**: `src/main/java/com/dt/example/hello/HelloWorld.java` — `main` prints `Hello, World!`.
- **Test entry**: `src/test/java/com/dt/example/hello/HelloWorldTest.java` — JUnit 5 + AssertJ, AAA layout.

## Project-specific review gates
- **Java version**: source/target must stay at 21; do not introduce APIs unavailable in JDK 21.
- **Build tooling**: only `mvn` is recognized for build/test verification (`mvn -q test`). No Gradle, no wrapper present.
- **Package convention**: production code under `com.dt.example.*`; tests mirror the package of the class under test.
- **Test framework**: JUnit 5 + AssertJ assertions only. No JUnit 4, no Hamcrest, no `assertTrue` equality hacks — use AssertJ `assertThat(...).isEqualTo(...)`.
- **File hygiene**: source files must end with a trailing newline (Maven/compiler convention).
- **Scope discipline**: this repo is a minimal example; changes should not pull in web/network/DB frameworks or extra Maven plugins unless the task explicitly requires them.

## Out of scope for this repo
- No CI workflow files present; do not invent CI-gate blockers.
- No README/AGENTS.md present; do not fail reviews for their absence.

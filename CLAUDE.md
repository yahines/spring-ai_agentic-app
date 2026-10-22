# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Maven wrapper is included (`mvnw` on Unix/Git Bash, `mvnw.cmd` on Windows). Java 21 is required.

- Run the app: `./mvnw spring-boot:run` (serves on port 8889)
- Build: `./mvnw clean package`
- Run all tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=AgenticAppApplicationTests`
- Run a single test method: `./mvnw test -Dtest=AgenticAppApplicationTests#contextLoads`

There is no linter or formatter configured.

## Configuration

- `OPENAI_API_KEY` must be set in the environment. `application.properties` references it as `${OPENAI_API_KEY}` with no default, so the Spring context (and therefore the tests that load it) will fail to start without it.
- Model is `gpt-4o-mini`, set via `spring.ai.openai.chat.options.model`.
- The Ollama starter is present in `pom.xml` but commented out. Only the OpenAI starter is active.

## Architecture

This is a small Spring Boot 4.1 / Spring AI 2.0 app with a single package root, `nesrine_dev.agenticapp`.

- `AgenticAppApplication` is the standard `@SpringBootApplication` entry point.
- `controllers/AIAgentController` is the only endpoint: `GET /chat?query=...` sends the query as a user message through a `ChatClient` and returns the plain-text response. The `ChatClient` is built from the auto-configured `ChatClient.Builder` injected in the constructor.
- The request flow is HTTP → controller → `ChatClient` → OpenAI, with no service layer, persistence, or prompt templating yet.

Dependencies that are on the classpath but not yet used by any code: Spring Data JPA with H2 (runtime), springdoc-openapi (Swagger UI is auto-served), and Lombok (annotation processor configured in `pom.xml`).

## Tests

`AgenticAppApplicationTests` is the only test, a `@SpringBootTest` that checks the context loads. There are no unit tests for the controller.
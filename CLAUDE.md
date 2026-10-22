# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Maven wrapper is included (`mvnw` on Unix/Git Bash, `mvnw.cmd` on Windows). Java 21 is required.

- Run the app: `./mvnw spring-boot:run` (serves on port 8899)
- Build: `./mvnw clean package`
- Run all tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=AgenticAppApplicationTests`
- Run a single test method: `./mvnw test -Dtest=AgenticAppApplicationTests#contextLoads`

There is no linter or formatter configured.

## Configuration

- `spring.ai.model.chat=ollama` — Ollama is the active chat model. `OpenAiChatAutoConfiguration` is disabled.
- `spring.ai.model.image=openai` — OpenAI DALL-E is auto-configured for image generation.
- `OPENAI_API_KEY` — optional at startup (`${OPENAI_API_KEY:none}`). Required for `/generateImage`.
- `STABILITY_API_KEY` — optional at startup (`${STABILITY_API_KEY:none}`). Required for `/generateImageSD`. Free account at platform.stability.ai.
- Ollama must be running locally on `http://localhost:11434`.
- Chat/vision model: `moondream` (1.8B). Switch to `llava:7b` for structured JSON output.
- Both `spring-ai-starter-model-openai` and `spring-ai-starter-model-ollama` are active. `spring.ai.model.chat=ollama` prevents `NoUniqueBeanDefinitionException`.
- `StabilityAiImageModel` is NOT auto-configured (auto-config requires `spring.ai.model.image=stabilityai`). It is created manually as a `@Bean` in `config/StableDiffusionConfig`.

## Architecture

Spring Boot 4.1 / Spring AI 2.0.1 app, package root: `nesrine_dev.agenticapp`.

### Controllers

- `AIAgentController` — `GET /chat?query=&conversationId=`  
  Chat with persistent memory via `MessageChatMemoryAdvisor`. `conversationId` defaults to `"default"`.

- `MultiModalAgentController` — multimodal endpoints:
  - `GET /describe` — describes `cni_fr.jpg` (specimen CNI), returns `CniModal` via `.entity()`. Requires a model that outputs JSON; `moondream` returns plain text and will fail here.
  - `POST /askImage` (multipart/form-data: `query`, `file`) — answers a question about an uploaded image.

- `AIStructuredAgentController` — structured output using `BeanOutputConverter` (movies list).

- `ImageGenerationController` — image generation endpoints:
  - `GET /generateImage?query=` — DALL-E 3 via `OpenAiImageModel`, returns a URL. Requires valid `OPENAI_API_KEY`.
  - Injects both `OpenAiImageModel` and `StabilityAiImageModel` (work in progress).

- `StableDiffusionController` — `GET /generateImageSD?query=`  
  Generates an image via Stability AI API (Stable Diffusion v1.6), returns binary `image/png`. Requires valid `STABILITY_API_KEY`.

- `StreamingController` — streaming chat response via SSE.

### Config

- `config/StableDiffusionConfig` — manually creates the `StabilityAiImageModel` bean using `${spring.ai.stabilityai.api-key}`.

### Outputs

- `CniModal` — `nom`, `prenom`, `dateNaissance`, `dateValidite`, `arabicFirstName`, `arabicLastName`, `numero`, `lieuNaissance`
- `Movie`, `MovieList` — used by `AIStructuredAgentController`

Test images in `src/main/resources/images/`: `cni_fr.jpg` (specimen CNI), `manuscrit.jpg`.

## Tests

`AgenticAppApplicationTests` — `@SpringBootTest` context load check only. No unit tests for controllers.

## Known limitations

- `moondream` cannot produce structured JSON — `/describe` fails with a parse error. Use `llava:7b` or OpenAI for that endpoint.
- Without a valid `OPENAI_API_KEY`, `/generateImage` returns 401 at call time (app starts fine).
- Without a valid `STABILITY_API_KEY`, `/generateImageSD` returns 401 at call time (app starts fine).
- `debug=true` is set in `application.properties` (verbose conditions evaluation report on startup).

# 06 — Roadmap

## Phase 0 — Monorepo baseline ✅

- Gradle Kotlin DSL, `apps/backend`, `apps/web`, `infra/ollama`

## Phase 1 — Backend ✅

- `LlmClient`, `OllamaLlmClient`, `MockLlmClient` (`mock-llm` profile)
- `ChatService`, `ConversationStore`, concurrency cap
- `POST /v1/chat` (SSE), `GET /v1/status`
- Actuator + `OllamaHealthIndicator`
- Static UI from `apps/web/dist` when built

## Phase 2 — UI + Ollama ✅

- `POST /v1/chat` streaming via `fetch`
- Sidebar, conversation history (`localStorage`)
- Thinking indicator, Stop (`AbortController`)
- Status bar (Ollama + server)

## Phase 3 — Polish ✅

- Integration tests (`@SpringBootTest`, mock profile)
- `ChatServiceTest` with StepVerifier
- Global exception handler
- `buildAll` Gradle task

## Optional future work

- Markdown rendering in assistant bubbles
- RAG / document upload
- Java 25 toolchain when fully supported
- `docker-compose` for Ollama + backend

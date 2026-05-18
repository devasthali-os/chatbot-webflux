# 06 — Roadmap

## Phase 0 — Monorepo baseline (done)

- [x] Gradle Kotlin DSL monorepo
- [x] `apps/backend` — Boot 3 WebFlux, `/heartbeat`, mock `/v2/chat`
- [x] `apps/web` — ChatGPT-like UI scaffold
- [x] `infra/ollama` — pull script + docs
- [x] Removed Maven `server/` and `client/`

## Phase 1 — Backend hardening

| Task | Details |
|------|---------|
| `LlmClient` + `OllamaLlmClient` | WebClient in `apps/backend` |
| `POST /v1/chat` | SSE + JSON body |
| `MockLlmClient` profile | Tests without Ollama |
| Serve UI from backend (optional) | Copy `apps/web/dist` to static resources |

**Exit criteria:** `./gradlew :apps:backend:build` + curl streams from Ollama.

## Phase 2 — UI + Ollama end-to-end

| Task | Details |
|------|---------|
| `useChatStream` → `POST /v1/chat` | Replace legacy `/v2/chat` in UI |
| Thinking indicator, Stop | `AbortController` |
| Status bar | Ollama + backend health |
| README runbook | Ollama → backend → web |

**Exit criteria:** Browser chat streams real local model on i7 Mac.

## Phase 3 — Polish

- Actuator, integration tests, conversation history (UI v2).
- Bump Java toolchain to 25 in `libs.versions.toml` when Spring supports it.

## Definition of done

1. Single Gradle monorepo for backend, web, and LLM infra.
2. Chat UI + Ollama on 2.2 GHz Intel i7 without cloud keys.
3. Design docs match `apps/*` and `infra/ollama` paths.

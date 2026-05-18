# 01 — Overview

## Problem statement

Build a chatbot application where users send messages and receive answers streamed back in real time. The backend is fully reactive, inference runs **locally** via Ollama, and a **ChatGPT-like web UI** is the primary interface.

## Source intent ([thoughts](../throughts.md))

| Requirement | Implication |
|-------------|-------------|
| Gradle + Kotlin DSL | Root `build.gradle.kts`, `settings.gradle.kts`, version catalog |
| Monorepo | `apps/backend`, `apps/web`, `infra/ollama` |
| Spring WebFlux, latest | Boot 3.4+ in `apps/backend` |
| Java 21+ (25 target) | Toolchain in `gradle/libs.versions.toml` |
| Local Ollama | `infra/ollama`, config in `application.yml` |
| 2.2 GHz quad-core i7 | Small models — [LLM Integration](04-llm-integration.md) |
| ChatGPT-like UI | `apps/web` — [UI Design](07-ui-design.md) |

## Goals

1. **ChatGPT-like UI** in `apps/web`.
2. **Streaming SSE** from `apps/backend` (`POST /v1/chat` target).
3. **Local-first** Ollama — no cloud API keys.
4. **Single repo** for backend, UI, and LLM ops docs/scripts.

## Non-goals (initial phases)

- Multi-tenant auth, RAG, fine-tuning.
- Separate `client/` CLI module (removed; UI + tests replace it).

## Current vs target

| Area | Current | Target |
|------|---------|--------|
| Build | Gradle Kotlin monorepo | ✅ |
| Backend | Boot 3 WebFlux, mock `/v2/chat` | `POST /v1/chat` + Ollama |
| UI | `apps/web` scaffold | Full streaming to Ollama |
| LLM | `infra/ollama` scripts | Wired via `LlmClient` |

## Success criteria

- `./gradlew :apps:backend:bootRun` + `./gradlew :apps:web:npm_run_dev`
- Browser chat streams responses from local Ollama on Intel i7.

# chatbot-webflux

Gradle Kotlin monorepo: **Spring WebFlux backend**, **ChatGPT-like web UI**, and **local Ollama** LLM.

```
chatbot-webflux/
├── apps/backend/      # Spring WebFlux API
├── apps/web/          # Vite + React chat UI
├── infra/ollama/      # Model pull scripts
└── docs/design/
```

## Prerequisites

- JDK 25 (Gradle auto-downloads via Foojay if not installed; Gradle **9.1+** required to run builds on JDK 25)
- Node.js 22+ (or Gradle downloads via Node plugin)
- [Ollama](https://ollama.com) on Intel Mac (`x86_64`)

## Quick start

```bash
# 1. LLM
ollama serve
./infra/ollama/pull-models.sh

# 2. Backend (default profile → Ollama)
./gradlew :apps:backend:bootRun

# 3. Web UI (separate terminal)
./gradlew :apps:web:npm_run_dev
# → http://localhost:5173
```

## API

| Endpoint | Description |
|----------|-------------|
| `GET /heartbeat` | Service health |
| `GET /v1/status` | Server + Ollama status |
| `POST /v1/chat` | Stream chat (SSE) — body: `{ "message", "conversationId?" }` |
| `GET /actuator/health` | Includes Ollama indicator |

## Gradle tasks

| Task | Description |
|------|-------------|
| `./gradlew buildAll` | Build UI + backend (UI copied into backend static/) |
| `./gradlew :apps:backend:bootRun` | API on :8080 |
| `./gradlew :apps:backend:test` | Tests (mock-llm profile) |
| `./gradlew :apps:web:npm_run_dev` | Vite on :5173 |

## Profiles

| Profile | Use |
|---------|-----|
| default | Ollama at `127.0.0.1:11434` |
| `mock-llm` | Tests / no Ollama — `./gradlew :apps:backend:bootRun --args='--spring.profiles.active=mock-llm'` |

## Hardware

Tuned for **2.2 GHz quad-core Intel i7** — default model `llama3.2:1b`. See [docs/design/04-llm-integration.md](docs/design/04-llm-integration.md).

## Design

[docs/design/README.md](docs/design/README.md)

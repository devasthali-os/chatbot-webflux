# chatbot-webflux

Gradle Kotlin monorepo: **reactive backend**, **ChatGPT-like web UI**, and **local Ollama** LLM.

```
chatbot-webflux/
├── apps/
│   ├── backend/     # Spring WebFlux (Java)
│   └── web/         # Vite + React chat UI
├── infra/
│   └── ollama/      # Local LLM setup scripts
├── docs/design/     # Architecture & API design
├── build.gradle.kts
└── settings.gradle.kts
```

## Prerequisites

- JDK 21+ (toolchain configured in Gradle)
- Node.js 22+ (or use Gradle Node plugin downloads)
- [Ollama](https://ollama.com) on Intel Mac (`x86_64`)

## Quick start

```bash
# 1. Local LLM
ollama serve
./infra/ollama/pull-models.sh

# 2. Backend
./gradlew :apps:backend:bootRun

# 3. Web UI (separate terminal)
./gradlew :apps:web:npm_run_dev
# → http://localhost:5173
```

## Gradle tasks

| Task | Description |
|------|-------------|
| `./gradlew :apps:backend:bootRun` | Start WebFlux API on :8080 |
| `./gradlew :apps:backend:test` | Backend tests |
| `./gradlew :apps:web:npm_run_dev` | Vite dev server on :5173 |
| `./gradlew :apps:web:buildWeb` | Production UI build |

## API (current)

- `GET /heartbeat` — health
- `GET /v2/chat` — mock SSE stream (legacy)
- `POST /v1/chat` — planned: Ollama streaming

## Design docs

[docs/design/README.md](docs/design/README.md)

## Related repos

- https://github.com/prayagupa/retailstore-microservice
- https://github.com/lamatola-os/netty-microservice
- https://github.com/prayagupa/nodejs-microservice

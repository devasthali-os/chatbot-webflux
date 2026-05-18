# Chatbot WebFlux — Design Documentation

Design docs derived from [thoughts](../throughts.md).

| Document | Description |
|----------|-------------|
| [Overview](01-overview.md) | Goals, constraints, and scope |
| [Architecture](02-architecture.md) | Monorepo layout, components, data flow |
| [API Design](03-api-design.md) | REST/SSE contracts |
| [LLM Integration](04-llm-integration.md) | Ollama on Intel i7 |
| [Technology Stack](05-technology-stack.md) | Gradle Kotlin DSL, Java, WebFlux |
| [Roadmap](06-roadmap.md) | Phased delivery |
| [Chat UI](07-ui-design.md) | ChatGPT-like browser UI |

## Monorepo layout

```
chatbot-webflux/
├── apps/
│   ├── backend/     # Spring WebFlux API
│   └── web/         # Vite + React chat UI
├── infra/
│   └── ollama/      # Local LLM scripts & docs
├── docs/design/
├── build.gradle.kts
└── settings.gradle.kts
```

## Quick summary

- **Gradle (Kotlin DSL)** monorepo — not Maven.
- **`apps/backend`** — WebFlux on Java 21+, streams from Ollama.
- **`apps/web`** — ChatGPT-like UI.
- **`infra/ollama`** — pull/run models on 2.2 GHz Intel i7.

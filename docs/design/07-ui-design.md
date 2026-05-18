# 07 — Chat UI (ChatGPT-like)

## Location

**`apps/web/`** — Vite + React + TypeScript, Gradle module `:apps:web`.

## Goal

Browser chat comparable to ChatGPT: message bubbles, streaming assistant text, bottom composer, Stop, status bar.

## Dev workflow

```bash
./gradlew :apps:backend:bootRun    # :8080
./gradlew :apps:web:npm_run_dev    # :5173
```

Vite proxies API paths to the backend (`vite.config.ts`).

## Structure

```
apps/web/
├── package.json
├── vite.config.ts
├── build.gradle.kts      # Node Gradle plugin
└── src/
    ├── App.tsx
    ├── api/chatClient.ts
    └── styles.css
```

## Streaming

- **Target:** `POST /v1/chat` with `fetch` + `ReadableStream`.
- **Current:** `GET /v2/chat` mock SSE until Phase 2.

## Production build

```bash
./gradlew :apps:web:buildWeb
```

Output: `apps/web/dist/` — can be copied to `apps/backend/src/main/resources/static/` for same-origin deploy.

## Visual design

Dark theme (`#212121`), user bubbles right, assistant left, green send button — see `src/styles.css`.

## Definition of done (UI)

1. `./gradlew :apps:web:npm_run_dev` opens working chat.
2. Streams assistant reply (mock now, Ollama in Phase 2).
3. Stop + server status in header.

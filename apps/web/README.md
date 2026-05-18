# Web — ChatGPT-like UI

Vite + React. Proxies API to backend `:8080` in dev.

```bash
# from repo root
./gradlew :apps:web:npm_run_dev

# or locally (Node 22+ recommended)
npm install && npm run dev
```

Open http://localhost:5173

Production build:

```bash
npm run build
# output: dist/ — bundled into backend static/ via :apps:backend:processResources
```

# Ollama (local LLM)

Runs on your **2.2 GHz quad-core Intel i7** Mac. Default model: **`llama3.2:1b`**.

```bash
brew install ollama
ollama serve
./infra/ollama/pull-models.sh
```

Verify:

```bash
curl http://127.0.0.1:11434/api/tags
ollama list
```

Configuration is in `apps/backend/src/main/resources/application.yml` under `chat.llm`.

---

## Default model: `llama3.2:1b`

Meta’s **Llama 3.2** family includes a **1B-parameter** instruct-tuned variant, distributed by Ollama as `llama3.2:1b`. This repo uses it as the default because it is the best trade-off on a **CPU-only Intel Mac** for an interactive chat UI: small enough to load quickly, capable enough for short Q&A, and fast enough to stream tokens without long pauses.

### Specs (typical Ollama pull)

| Property | Value |
|----------|--------|
| Parameters | ~1.2B |
| Quantization | Q8_0 (default tag) |
| Download size | ~1.3 GB |
| RAM while running | ~2 GB |
| Context window | 128K tokens (Ollama); backend caps output via `max-tokens` |

### Pull and smoke-test

```bash
ollama pull llama3.2:1b
ollama run llama3.2:1b "Say hello in one sentence."
```

Or use the project script (same model by default):

```bash
./infra/ollama/pull-models.sh
# ./infra/ollama/pull-models.sh llama3.2:1b   # explicit
```

### Expected performance (2.2 GHz i7, CPU)

| Metric | Typical range |
|--------|----------------|
| Time to first token | ~2–5 s |
| Streaming speed | ~5–15 tokens/s |
| Concurrent chats | 1 stream recommended |

The web UI shows a “Thinking…” state until the first SSE chunk arrives; on this hardware a short delay is normal.

### Backend settings

`application.yml` points the backend at this model:

```yaml
chat:
  llm:
    base-url: http://127.0.0.1:11434
    model: llama3.2:1b
    max-tokens: 384
```

To try another tag temporarily, override the model name in config or pass a model field on `POST /v1/chat` if the API supports it.

### When to use something else

| Situation | Alternative |
|-----------|-------------|
| **8 GB RAM**, machine feels tight | `tinyllama` — smaller, faster, lower quality |
| **16 GB RAM**, want better answers | `phi3:mini` — ~2.3 GB disk, slower first token |
| **7B+ models** (`llama3:8b`, `mistral:7b`) | Usually too slow for real-time chat on this CPU |

```bash
./infra/ollama/pull-models.sh tinyllama
```

### Troubleshooting

| Symptom | Fix |
|---------|-----|
| Model not in `ollama list` | `ollama pull llama3.2:1b` |
| `connection refused` on :11434 | Run `ollama serve` |
| Very slow / high fan | Lower `max-tokens`; close heavy apps; try `tinyllama` |
| Wrong model name in errors | Tag must be exactly `llama3.2:1b` (see `ollama list`) |

More tuning and API notes: [docs/design/04-llm-integration.md](../../docs/design/04-llm-integration.md).

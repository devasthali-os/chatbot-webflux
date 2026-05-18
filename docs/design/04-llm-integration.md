# 04 — LLM Integration (Local, Intel Mac)

## Requirements recap

- LLM runs **on the same machine** as the chat server (development and demo).
- **No requirement** for GPT-4, Claude Opus, or other frontier cloud models.
- Must be viable on **Apple Intel** (x86_64), CPU inference, no discrete GPU assumption.
- Must work on the **developer machine** profile below.

## Developer hardware profile

| Spec | Your machine | Design impact |
|------|----------------|---------------|
| CPU | **2.2 GHz Quad-Core Intel Core i7** (4 cores, 8 threads with Hyper-Threading) | CPU-only inference; expect modest tokens/sec |
| Typical RAM | 8 GB or 16 GB (verify: `sysctl hw.memsize`) | Caps maximum model size |
| GPU | Intel Iris Pro / integrated only | No Metal/CUDA offload for Ollama on Intel |
| OS | macOS 12+ (darwin 21.x) | Ollama Intel builds supported |

**Verdict: Ollama is supported and appropriate** on this Mac. Use **small quantized models** only; treat 7B+ models as experimental, not default.

### Ollama compatibility checklist (your Mac)

1. **Architecture:** Intel Mac → install the **x86_64** build from [ollama.com](https://ollama.com) or `brew install ollama` (Homebrew bottle for Intel).
2. **macOS:** Monterey (12) or newer recommended for current Ollama releases.
3. **Disk:** Reserve **~5 GB** free (app + one small model + headroom).
4. **RAM:**
   - **16 GB system RAM:** default model **`phi3:mini`** or **`llama3.2:1b`**.
   - **8 GB system RAM:** default **`tinyllama`** only; close other heavy apps while chatting.
5. **Verify install:**

```bash
uname -m          # expect: x86_64
ollama --version
ollama serve      # keep running in a terminal, or use background service
curl http://127.0.0.1:11434/api/tags
```

If `uname -m` shows `arm64`, you are on Apple Silicon — different doc tuning applies; this project targets **Intel x86_64**.

### Recommended models (2.2 GHz i7)

| Model | Pull command | Disk | RAM (approx) | Expected UX on your CPU |
|-------|----------------|------|--------------|-------------------------|
| **Default** | `ollama pull llama3.2:1b` | ~1.3 GB | ~2 GB | Best balance for ChatGPT-like UI |
| **Fastest** | `ollama pull tinyllama` | ~637 MB | ~1 GB | Fastest replies; lower quality |
| **Better quality** | `ollama pull phi3:mini` | ~2.3 GB | ~3 GB | Usable; first token slower |
| **Avoid default** | `mistral:7b*`, `llama3:8b` | 4–8 GB | 6 GB+ | Often **too slow** for interactive chat |

```bash
# Recommended first-time setup for your machine
brew install ollama
ollama pull llama3.2:1b
ollama run llama3.2:1b "Say hello in one sentence."
```

**Tuning for responsiveness** (Ollama modelfile or API options):

- `num_predict` / `max_tokens`: **256–384** (UI feels snappier).
- Keep prompts short in v1 (no huge system prompts).
- One chat stream at a time on this hardware.

### Rough latency expectations (2.2 GHz i7, CPU)

| Model | Time to first token | Steady streaming |
|-------|---------------------|------------------|
| `tinyllama` | ~1–3 s | ~8–20 tokens/s (varies) |
| `llama3.2:1b` | ~2–5 s | ~5–15 tokens/s |
| `phi3:mini` | ~3–8 s | ~3–10 tokens/s |
| `7b` quantized | ~10–30+ s | Often unsuitable for UI |

The [Chat UI](07-ui-design.md) must show a **thinking indicator** until the first chunk arrives.

## Recommended runtime: Ollama

**Why Ollama**

- Official **macOS Intel (x86_64)** support.
- Single install; manages model downloads.
- **Streaming** native REST API — fits WebFlux SSE and the browser UI.
- Default listen: `http://127.0.0.1:11434`

**Install (Intel Mac)**

```bash
# https://ollama.com/download — choose Mac (Intel)
brew install ollama
ollama serve
```

**Not recommended on this hardware**

| Model | Why |
|-------|-----|
| `mistral:7b-instruct-q4_0` | High RAM + slow on 2.2 GHz i7 |
| `llama3:8b` | Same |
| Multiple concurrent `ollama run` | Contends for 4 cores |

## Alternatives

| Runtime | Pros | Cons |
|---------|------|------|
| **LocalAI** | OpenAI-compatible | Heavier setup |
| **llama.cpp server** | Minimal | Manual model paths |
| **GPT4All** | Desktop GUI | Less ideal for headless + WebFlux |

**Decision:** **Ollama** for v1; abstract behind `LlmClient`.

## HTTP integration from Spring WebFlux

### Native Ollama chat API (streaming)

```http
POST http://localhost:11434/api/chat
Content-Type: application/json
```

```json
{
  "model": "llama3.2:1b",
  "messages": [
    { "role": "user", "content": "What is WebFlux?" }
  ],
  "stream": true,
  "options": {
    "num_predict": 384
  }
}
```

**Response:** newline-delimited JSON; each line may include `message.content`; final line has `done: true`.

**WebClient sketch:**

```java
webClient.post()
    .uri("/api/chat")
    .bodyValue(ollamaRequest)
    .retrieve()
    .bodyToFlux(OllamaStreamLine.class)
    .mapNotNull(line -> line.message() != null ? line.message().content() : null)
    .filter(Objects::nonNull);
```

Run on `Schedulers.boundedElastic()` or the app `ioScheduler` if any step blocks.

## Prompting strategy (v1)

- **System prompt:** Short — helpful assistant, concise answers (keeps generation fast on CPU).
- **User message:** From `ChatRequest.message`.
- **History:** Omit in v1; add with `conversationId` when UI sidebar lands (v2).

## Configuration

```yaml
chat:
  llm:
    provider: ollama
    base-url: http://127.0.0.1:11434
    model: llama3.2:1b    # tuned for 2.2 GHz quad-core i7
    connect-timeout: 5s
    read-timeout: 180s    # CPU inference can be slow to start
    max-tokens: 384
```

## Performance rules (Intel i7)

- Do **not** block Netty event loops waiting for Ollama.
- Use **Netty-only** WebFlux after Boot 3 migration.
- Server can handle many **heartbeat** connections; limit **concurrent chat streams** to **1–2** on this hardware (configurable semaphore).
- UI streams via SSE; see [UI Design](07-ui-design.md).

## Mock LLM for CI / offline

`MockLlmClient` + profile `mock-llm` when Ollama is not installed (fixed `Flux` chunks).

## Operational checklist

1. `uname -m` → `x86_64`.
2. `ollama serve` running.
3. `ollama pull llama3.2:1b` (or `tinyllama` on 8 GB RAM).
4. `curl http://127.0.0.1:11434/api/tags` lists the model.
5. `GET /heartbeat` OK.
6. `POST /v1/chat` streams tokens.
7. Open Chat UI — message streams in the assistant bubble.

## Troubleshooting (your hardware)

| Symptom | Action |
|---------|--------|
| `ollama: command not found` | `brew install ollama` or install Intel binary from ollama.com |
| Very slow / fans maxed | Switch to `tinyllama`; lower `max-tokens` |
| OOM / Mac freezes | Model too large — use `tinyllama`; quit browsers/IDEs |
| `connection refused :11434` | Start `ollama serve` |
| First message fast, then slow | Thermal throttling — normal on laptop CPUs; pause between long prompts |

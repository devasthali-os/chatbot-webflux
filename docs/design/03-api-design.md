# 03 — API Design

## Versioning

- **`/heartbeat`** — Keep for ops/load tests (existing).
- **`/v1/chat`** — New canonical chat API (user message in, streamed reply out).
- **`/v2/chat`** — Legacy prototype (GET, mock messages); deprecate after `/v1` is stable.

## Health

### `GET /heartbeat`

Unchanged semantics from prototype.

**Response:** `application/json`

```json
{
  "service": "chat-server",
  "version": "1.0"
}
```

**Type:** `Mono<HealthResponse>`

---

## Streaming chat (target)

### `POST /v1/chat`

Send a user message; receive the assistant reply as SSE.

**Request**

```
POST /v1/chat HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: text/event-stream
```

```json
{
  "message": "What is reactive programming?",
  "conversationId": "optional-uuid",
  "model": "optional-override"
}
```

| Field | Required | Description |
|-------|----------|-------------|
| `message` | Yes | User text |
| `conversationId` | No | Future multi-turn; v1 may ignore |
| `model` | No | Overrides `chat.llm.model` |

**Response:** `text/event-stream`

Each event is a JSON object compatible with existing `ChatResponse`:

```json
{"message":"Reactive "}
{"message":"programming is..."}
```

**SSE framing (recommended explicit format):**

```
data: {"message":"Reactive "}\n\n
data: {"message":"programming is..."}\n\n
```

**Errors before stream starts:** `4xx` / `5xx` with `application/json` body:

```json
{
  "error": "LLM_UNAVAILABLE",
  "message": "Cannot connect to Ollama at localhost:11434"
}
```

**Implementation note:** Spring can return `Flux<ServerSentEvent<ChatResponse>>` or `Flux<ChatResponse>` with `produces = TEXT_EVENT_STREAM_VALUE` (prototype uses the latter).

---

## Legacy prototype

### `GET /v2/chat`

**Status:** Mock stream — three canned messages, 1s apart (`ChatController.chatResponsePublisher`).

**Response:** `text/event-stream` (intended; some clients historically saw `application/json` — fix during upgrade).

**Migration:** Clients move to `POST /v1/chat`; remove GET mock in a later milestone.

---

## Client consumption

### WebClient (reactive)

Pattern from `SpringHttpClient`:

```java
webClient.post()
    .uri("/v1/chat")
    .contentType(MediaType.APPLICATION_JSON)
    .accept(MediaType.TEXT_EVENT_STREAM)
    .bodyValue(new ChatRequest("Hello"))
    .retrieve()
    .bodyToFlux(ChatResponse.class)
    .subscribe(chunk -> System.out.println(chunk.getMessage()));
```

### curl

```bash
curl -N -X POST http://localhost:8080/v1/chat \
  -H "Content-Type: application/json" \
  -H "Accept: text/event-stream" \
  -d '{"message":"Hello"}'
```

---

## DTOs

| Type | Fields | Notes |
|------|--------|-------|
| `ChatRequest` | `message`, `conversationId?`, `model?` | New |
| `ChatResponse` | `message` | Exists; may add `done`, `tokenIndex` later |
| `HealthResponse` | `service`, `version` | Exists |
| `ErrorResponse` | `error`, `message` | New |

---

## Browser UI (ChatGPT-like)

The primary consumer is **`apps/web`** ([UI Design](07-ui-design.md)).

| Concern | Approach |
|---------|----------|
| Transport | `POST /v1/chat` + `Accept: text/event-stream` |
| Client API | `fetch` + `ReadableStream` (supports POST body + **AbortController** for Stop) |
| Dev networking | Vite proxy to `:8080` or WebFlux `CorsWebFilter` for `http://localhost:5173` |
| Prod | Serve UI build from `classpath:/static/` — same origin, no CORS |

**Optional:** `GET /v1/status` returning `{ "server": "up", "ollama": "up", "model": "llama3.2:1b" }` for the UI status bar.

---

## OpenAPI (optional)

Generate OpenAPI 3.1 from Springdoc WebFlux for documentation and client codegen — recommended in phase 2, not blocking phase 1.

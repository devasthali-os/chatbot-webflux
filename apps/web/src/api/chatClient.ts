interface ChatChunk {
  message?: string;
}

/** Uses legacy GET /v2/chat until POST /v1/chat + Ollama is wired. */
export async function streamLegacyChat(
  onChunk: (text: string) => void,
  signal?: AbortSignal
): Promise<void> {
  const res = await fetch("/v2/chat", {
    method: "GET",
    headers: { Accept: "text/event-stream" },
    signal,
  });
  if (!res.ok || !res.body) {
    throw new Error(`Chat failed: ${res.status}`);
  }

  const reader = res.body.getReader();
  const decoder = new TextDecoder();
  let buffer = "";

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    buffer += decoder.decode(value, { stream: true });
    const lines = buffer.split("\n");
    buffer = lines.pop() ?? "";

    for (const line of lines) {
      const trimmed = line.trim();
      if (!trimmed.startsWith("data:")) continue;
      const json = trimmed.slice(5).trim();
      if (!json) continue;
      try {
        const chunk = JSON.parse(json) as ChatChunk;
        if (chunk.message) onChunk(chunk.message);
      } catch {
        // Spring may emit raw JSON lines without SSE prefix during dev
        try {
          const chunk = JSON.parse(trimmed) as ChatChunk;
          if (chunk.message) onChunk(chunk.message);
        } catch {
          /* skip malformed line */
        }
      }
    }
  }
}

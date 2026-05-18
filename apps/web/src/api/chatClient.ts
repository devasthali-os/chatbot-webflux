import type { ChatChunk, StatusResponse } from "../types";

export async function fetchStatus(): Promise<StatusResponse> {
  const res = await fetch("/v1/status");
  if (!res.ok) throw new Error(`Status failed: ${res.status}`);
  return res.json() as Promise<StatusResponse>;
}

export async function streamChat(
  message: string,
  conversationId: string | undefined,
  onChunk: (chunk: ChatChunk) => void,
  signal?: AbortSignal
): Promise<void> {
  const res = await fetch("/v1/chat", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "text/event-stream",
    },
    body: JSON.stringify({ message, conversationId }),
    signal,
  });

  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error((err as { message?: string }).message ?? "Chat failed");
  }
  if (!res.body) throw new Error("No response body");

  const reader = res.body.getReader();
  const decoder = new TextDecoder();
  let buffer = "";

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    buffer += decoder.decode(value, { stream: true });
    const events = buffer.split("\n\n");
    buffer = events.pop() ?? "";

    for (const event of events) {
      for (const line of event.split("\n")) {
        const trimmed = line.trim();
        if (!trimmed.startsWith("data:")) continue;
        const json = trimmed.slice(5).trim();
        if (!json) continue;
        onChunk(JSON.parse(json) as ChatChunk);
      }
    }
  }
}

import { useCallback, useEffect, useRef, useState } from "react";
import { streamLegacyChat } from "./api/chatClient";

type Role = "user" | "assistant";

interface Message {
  id: string;
  role: Role;
  content: string;
}

export function App() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");
  const [streaming, setStreaming] = useState(false);
  const [serverUp, setServerUp] = useState<boolean | null>(null);
  const abortRef = useRef<AbortController | null>(null);

  useEffect(() => {
    fetch("/heartbeat")
      .then((r) => setServerUp(r.ok))
      .catch(() => setServerUp(false));
  }, []);

  const send = useCallback(async () => {
    const text = input.trim();
    if (!text || streaming) return;

    setInput("");
    setStreaming(true);
    const userId = crypto.randomUUID();
    const assistantId = crypto.randomUUID();
    setMessages((m) => [
      ...m,
      { id: userId, role: "user", content: text },
      { id: assistantId, role: "assistant", content: "" },
    ]);

    const controller = new AbortController();
    abortRef.current = controller;

    try {
      await streamLegacyChat((chunk) => {
        setMessages((m) =>
          m.map((msg) =>
            msg.id === assistantId ? { ...msg, content: msg.content + chunk } : msg
          )
        );
      }, controller.signal);
    } catch (e) {
      if ((e as Error).name !== "AbortError") {
        setMessages((m) =>
          m.map((msg) =>
            msg.id === assistantId
              ? { ...msg, content: msg.content || "Error: could not reach backend." }
              : msg
          )
        );
      }
    } finally {
      setStreaming(false);
      abortRef.current = null;
    }
  }, [input, streaming]);

  const stop = () => abortRef.current?.abort();

  return (
    <div className="app">
      <header className="header">
        <h1>Local Chat</h1>
        <span className="status">
          llama3.2:1b · Ollama · Server {serverUp === null ? "…" : serverUp ? "●" : "○"}
        </span>
      </header>

      <main className="messages" role="log" aria-live="polite">
        {messages.length === 0 && (
          <p className="welcome">Ask anything — powered by local Ollama on your Mac.</p>
        )}
        {messages.map((m) => (
          <div key={m.id} className={`bubble ${m.role}`}>
            {m.content || (m.role === "assistant" && streaming ? "Thinking…" : "")}
          </div>
        ))}
      </main>

      <footer className="composer">
        <textarea
          aria-label="Message"
          value={input}
          rows={1}
          placeholder="Message…"
          disabled={streaming}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter" && !e.shiftKey) {
              e.preventDefault();
              send();
            }
          }}
        />
        {streaming ? (
          <button type="button" onClick={stop}>
            Stop
          </button>
        ) : (
          <button type="button" onClick={send} disabled={!input.trim()}>
            Send
          </button>
        )}
      </footer>
    </div>
  );
}

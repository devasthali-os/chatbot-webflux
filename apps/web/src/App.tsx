import { useCallback, useEffect, useState } from "react";
import { fetchStatus } from "./api/chatClient";
import { useChatStream } from "./hooks/useChatStream";
import {
  createConversation,
  loadConversations,
  saveConversations,
  titleFromMessage,
} from "./storage/conversations";
import type { Conversation, StatusResponse } from "./types";

export function App() {
  const [conversations, setConversations] = useState<Conversation[]>(() => loadConversations());
  const [activeId, setActiveId] = useState<string | null>(
    () => loadConversations()[0]?.id ?? null
  );
  const [input, setInput] = useState("");
  const [status, setStatus] = useState<StatusResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const { streaming, send, stop } = useChatStream();

  const active = conversations.find((c) => c.id === activeId) ?? null;

  const refreshStatus = useCallback(() => {
    fetchStatus()
      .then(setStatus)
      .catch(() => setStatus(null));
  }, []);

  useEffect(() => {
    refreshStatus();
    const id = setInterval(refreshStatus, 30_000);
    return () => clearInterval(id);
  }, [refreshStatus]);

  useEffect(() => {
    saveConversations(conversations);
  }, [conversations]);

  const newChat = () => {
    const c = createConversation();
    setConversations((list) => [c, ...list]);
    setActiveId(c.id);
    setError(null);
  };

  const onSend = async () => {
    const text = input.trim();
    if (!text || streaming) return;

    let convId = activeId;
    if (!convId) {
      const c = createConversation();
      setConversations((list) => [c, ...list]);
      convId = c.id;
      setActiveId(c.id);
    }

    const isFirstMessage = !active?.messages.length;
    if (isFirstMessage) {
      setConversations((list) =>
        list.map((c) => (c.id === convId ? { ...c, title: titleFromMessage(text) } : c))
      );
    }

    setInput("");
    setError(null);

    try {
      await send(text, convId, (updater) => {
        setConversations((list) =>
          list.map((c) =>
            c.id === convId
              ? { ...c, messages: updater(c.messages), updatedAt: Date.now() }
              : c
          )
        );
      });
    } catch (e) {
      if ((e as Error).name !== "AbortError") {
        setError((e as Error).message);
      }
    }
  };

  const model = status?.model ?? "llama3.2:1b";

  return (
    <div className="layout">
      <aside className="sidebar">
        <button type="button" className="new-chat" onClick={newChat}>
          + New chat
        </button>
        <ul className="conv-list">
          {conversations.map((c) => (
            <li key={c.id}>
              <button
                type="button"
                className={c.id === activeId ? "active" : ""}
                onClick={() => {
                  setActiveId(c.id);
                  setError(null);
                }}
              >
                {c.title}
              </button>
            </li>
          ))}
        </ul>
      </aside>

      <div className="main">
        <header className="header">
          <h1>Local Chat</h1>
          <span className="status">
            {model} · Ollama {status?.ollama ? "●" : "○"} · Server{" "}
            {status?.server === "up" ? "●" : "○"}
          </span>
        </header>

        {error && <div className="error-banner">{error}</div>}

        <main className="messages" role="log" aria-live="polite">
          {!active?.messages.length && (
            <p className="welcome">Ask anything — runs locally via Ollama on your Mac.</p>
          )}
          {active?.messages.map((m) => (
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
                onSend();
              }
            }}
          />
          {streaming ? (
            <button type="button" onClick={stop}>
              Stop
            </button>
          ) : (
            <button type="button" onClick={onSend} disabled={!input.trim()}>
              Send
            </button>
          )}
        </footer>
      </div>
    </div>
  );
}

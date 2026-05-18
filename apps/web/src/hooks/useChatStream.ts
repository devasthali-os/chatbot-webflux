import { useCallback, useRef, useState } from "react";
import { streamChat } from "../api/chatClient";
import type { Message } from "../types";

export function useChatStream() {
  const [streaming, setStreaming] = useState(false);
  const abortRef = useRef<AbortController | null>(null);

  const stop = useCallback(() => {
    abortRef.current?.abort();
  }, []);

  const send = useCallback(
    async (
      userText: string,
      conversationId: string,
      onMessagesUpdate: (updater: (messages: Message[]) => Message[]) => void
    ) => {
      const assistantId = crypto.randomUUID();
      onMessagesUpdate((m) => [
        ...m,
        { id: crypto.randomUUID(), role: "user", content: userText },
        { id: assistantId, role: "assistant", content: "" },
      ]);

      setStreaming(true);
      const controller = new AbortController();
      abortRef.current = controller;

      try {
        await streamChat(
          userText,
          conversationId,
          (chunk) => {
            if (chunk.done) return;
            if (!chunk.message) return;
            onMessagesUpdate((m) =>
              m.map((msg) =>
                msg.id === assistantId
                  ? { ...msg, content: msg.content + chunk.message }
                  : msg
              )
            );
          },
          controller.signal
        );
      } finally {
        setStreaming(false);
        abortRef.current = null;
      }
    },
    []
  );

  return { streaming, send, stop };
}

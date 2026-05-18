export type Role = "user" | "assistant";

export interface Message {
  id: string;
  role: Role;
  content: string;
}

export interface Conversation {
  id: string;
  title: string;
  messages: Message[];
  updatedAt: number;
}

export interface ChatChunk {
  message?: string;
  done?: boolean;
}

export interface StatusResponse {
  server: string;
  version: string;
  ollama: boolean;
  model: string;
}

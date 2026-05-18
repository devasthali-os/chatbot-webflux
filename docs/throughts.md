create a chatbot app backed by Spring webflux latest version on java 25. Webflux talks to locally running LLM. LLM does not have to be current LLM like GPT or Opus models. Run something that generates response in Apple Intel processor.

- Machine: 2.2 GHz Quad-Core Intel Core i7
- UI: ChatGPT-like web chat (`apps/web`)
- Backend: Spring WebFlux (`apps/backend`)
- Build: Gradle Kotlin DSL monorepo
- LLM: Ollama (`infra/ollama`, e.g. llama3.2:1b)

---

**Design docs:** [docs/design/README.md](design/README.md)
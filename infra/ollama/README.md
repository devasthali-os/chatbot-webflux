# Ollama (local LLM)

Runs on your **2.2 GHz quad-core Intel i7** Mac. Default model: `llama3.2:1b`.

```bash
brew install ollama
ollama serve
./infra/ollama/pull-models.sh
```

Verify:

```bash
curl http://127.0.0.1:11434/api/tags
```

Configuration is in `apps/backend/src/main/resources/application.yml` under `chat.llm`.

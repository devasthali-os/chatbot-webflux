# Backend — Spring WebFlux

Reactive API that will stream chat from local Ollama.

```bash
# Auto-reload on Java / application.yml changes
./gradlew :apps:backend:dev

# Or manual bootRun (pair with compile watcher for faster DevTools restarts)
./gradlew :apps:backend:bootRun
./gradlew :apps:backend:classes --continuous   # second terminal

curl localhost:8080/heartbeat
```

Package root: `com.chatbot.backend`

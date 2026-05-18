# 05 — Technology Stack

## Monorepo build

| Layer | Choice |
|-------|--------|
| Build | **Gradle 8.14** with **Kotlin DSL** |
| Version catalog | `gradle/libs.versions.toml` |
| JVM apps | `apps/backend` |
| Node apps | `apps/web` via `com.github.node-gradle.node` |

## Backend (`apps/backend`)

| Layer | Choice |
|-------|--------|
| Language | Java 21 (toolchain; bump to 25 in catalog when ready) |
| Framework | Spring Boot 3.4.5 |
| Reactive web | `spring-boot-starter-webflux` only (Netty) |
| HTTP client | WebClient → Ollama |
| Build file | `apps/backend/build.gradle.kts` |

```kotlin
// apps/backend/build.gradle.kts
plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}
```

## Web UI (`apps/web`)

| Layer | Choice |
|-------|--------|
| Runtime | Node 22 (Gradle plugin can download) |
| Bundler | Vite 6 |
| UI | React 19 + TypeScript |
| Dev proxy | `/v1`, `/v2`, `/heartbeat` → `:8080` |

Gradle tasks: `npm_run_dev`, `buildWeb`.

## LLM (`infra/ollama`)

| Layer | Choice |
|-------|--------|
| Runtime | Ollama x86_64 on Intel Mac |
| Default model | `llama3.2:1b` |
| Scripts | `infra/ollama/pull-models.sh` |

## Deliberate exclusions

- Maven / `pom.xml` — removed.
- `spring-boot-starter-web` (Tomcat) — not used.
- Legacy `server/`, `client/` folders — replaced by `apps/*`.

## Testing

| Module | Command |
|--------|---------|
| Backend | `./gradlew :apps:backend:test` |
| Web | `cd apps/web && npm run build` |

## Observability (planned)

- Actuator + `OllamaHealthIndicator` in `apps/backend`.

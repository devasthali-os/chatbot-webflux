plugins {
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.node) apply false
}

subprojects {
    group = "com.chatbot"
    version = "0.1.0-SNAPSHOT"
}

tasks.register("buildAll") {
    group = "build"
    description = "Build backend and web UI"
    dependsOn(":apps:web:buildWeb", ":apps:backend:build")
}

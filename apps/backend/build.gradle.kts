plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

dependencies {
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    testImplementation(libs.reactor.test)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveBaseName.set("chatbot-backend")
}

tasks.named<ProcessResources>("processResources") {
    val webBuild = rootProject.layout.projectDirectory.dir("apps/web/dist")
    if (webBuild.asFile.exists()) {
        from(webBuild) {
            into("static")
        }
    }
}

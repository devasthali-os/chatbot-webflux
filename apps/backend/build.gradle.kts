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
    developmentOnly(libs.spring.boot.devtools)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux.test)
    testImplementation(libs.reactor.test)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    sourceResources(sourceSets.named("main").get())
}

tasks.register<Exec>("dev") {
    group = "application"
    description = "Run the API; recompiles and restarts when Java or config files change"
    workingDir = rootProject.layout.projectDirectory.asFile
    val isWindows = System.getProperty("os.name").lowercase().contains("windows")
    val gradlew = if (isWindows) "gradlew.bat" else "gradlew"
    commandLine(
        rootProject.layout.projectDirectory.file(gradlew).asFile.absolutePath,
        ":apps:backend:bootRun",
        "--continuous",
    )
    standardInput = System.`in`
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

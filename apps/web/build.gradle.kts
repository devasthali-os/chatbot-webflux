plugins {
    alias(libs.plugins.node)
}

node {
    version.set(libs.versions.node.get())
    npmVersion.set(libs.versions.npm.get())
    download.set(System.getenv("CI") == null) // use system Node in CI if download fails
}

tasks.register("buildWeb") {
    dependsOn("npm_run_build")
    group = "build"
    description = "Production build of the chat UI"
}

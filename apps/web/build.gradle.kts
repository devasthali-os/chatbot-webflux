plugins {
    alias(libs.plugins.node)
}

node {
    version.set(libs.versions.node.get())
    npmVersion.set(libs.versions.npm.get())
    download.set(true)
}

tasks.register("buildWeb") {
    dependsOn("npm_run_build")
    group = "build"
    description = "Production build of the chat UI"
}

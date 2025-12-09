// Minimal root build script. Plugin versions live in settings.gradle.kts (pluginManagement).
plugins {
    // No root plugins required here; all module plugins are declared in settings.gradle.kts
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    plugins {
        // Declare plugin coordinates & versions here so modules can apply them with the plugins DSL.
        id("com.android.application") version "8.3.2" apply false
        id("org.jetbrains.kotlin.android") version "1.9.24" apply false
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24" apply false
        id("com.google.gms.google-services") version "4.4.4" apply false
    }
}

dependencyResolutionManagement {
    // Recommended: fail if modules try to declare their own repositories
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "HandyHood"
include(":app")

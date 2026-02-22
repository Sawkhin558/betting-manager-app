pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    
    plugins {
        id("com.android.application") version "8.3.2"
        id("org.jetbrains.kotlin.android") version "1.9.23"
        id("com.google.dagger.hilt.android") version "2.50"
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
        id("org.owasp.dependencycheck") version "9.0.9"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    
    versionCatalogs {
        create("libs") {
            // Android Core
            version("androidx-core", "1.12.0")
            version("androidx-lifecycle", "2.7.0")
            version("androidx-activity", "1.8.2")
            
            // Compose
            version("compose-bom", "2024.02.01")
            version("compose-compiler", "1.5.11")
            
            // Room
            version("room", "2.6.1")
            
            // Hilt
            version("hilt", "2.50")
            
            // Testing
            version("junit", "4.13.2")
            version("androidx-test", "1.5.0")
            version("androidx-espresso", "3.5.1")
        }
    }
}

rootProject.name = "BettingManager"
include(":app")
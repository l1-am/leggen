pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version providers.gradleProperty("plugins.foojay.version")
        id("net.fabricmc.fabric-loom") version providers.gradleProperty("plugins.loom.version")
        id("net.neoforged.moddev") version providers.gradleProperty("plugins.moddev.version")
    }
}

rootProject.name = "leggen"

include("common", "fabric", "neoforge")

// Gradle 10 preview features
enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")
enableFeaturePreview("ENHANCED_GRAPH_ORDERING")

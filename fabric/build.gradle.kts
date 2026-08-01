plugins {
    id("multiloader-loader")
    id("net.fabricmc.fabric-loom")
}

val minecraftVersion = providers.gradleProperty("minecraft_version").get()
val fabricLoaderVersion = providers.gradleProperty("fabric_loader_version").get()
val fabricApiVersion = providers.gradleProperty("fabric_api_version").get()
val projectId = providers.gradleProperty("project_id").get()

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
}

loom {
    val accessWidener = project(":common").file("src/main/resources/$projectId.accesswidener")
    if (accessWidener.exists()) {
        accessWidenerPath.set(accessWidener)
    }

    runs {
        named("client") {
            client()
            displayName.set("Fabric Client")
            generateRunConfig.set(true)
            runDirectory.set(layout.projectDirectory.dir("runs/client"))
        }
        named("server") {
            server()
            displayName.set("Fabric Server")
            generateRunConfig.set(true)
            runDirectory.set(layout.projectDirectory.dir("runs/server"))
        }
    }
}

LoaderAttributeHelper.addCommonLoaderAttributes(project, "fabric", configurations.includeInternal, configurations.modCompileClasspath)

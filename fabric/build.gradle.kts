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
        accessWidenerPath = accessWidener
    }

    runs {
        configureEach {
            generateRunConfig = true
            preferGradleTask = true
            runDirectory = layout.projectDirectory.dir("runs/$name")

            displayName = "Fabric ${name.replaceFirstChar(Char::uppercase)}"
        }

        named("client") { client() }
        named("server") { server() }
    }
}

LoaderAttributeHelper.addLoaderAttributes(project, "fabric", configurations.includeInternal, configurations.modCompileClasspath)

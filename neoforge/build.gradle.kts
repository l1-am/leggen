plugins {
    id("multiloader-loader")
    id("net.neoforged.moddev")
}

val neoforgeVersion = providers.gradleProperty("neoforge_version").get()
val projectId = providers.gradleProperty("project_id").get()

neoForge {
    version = neoforgeVersion

    val accessTransformer = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")

    if (accessTransformer.exists()) {
        accessTransformers.from(accessTransformer.absolutePath)
    }

    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", projectId)

            ideName = "NeoForge ${name.replaceFirstChar(Char::uppercase)} (${project.path})"
            gameDirectory = layout.projectDirectory.dir("runs/$name")
        }

        create("client") { client() }
        create("server") { server() }

        create("data") {
            clientData()

            programArguments.addAll(
                "--mod",
                projectId,
                "--all",
                "--output",
                file("src/generated/resources").absolutePath,
                "--existing",
                file("src/main/resources").absolutePath,
            )
        }
    }

    mods {
        create(projectId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

LoaderAttributeHelper.addLoaderAttributes(project, "neoforge")

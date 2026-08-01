import org.gradle.api.attributes.Attribute

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
            ideName.set("NeoForge ${name.replaceFirstChar(Char::uppercase)} (${project.path})")
        }
        create("client") {
            client()
            gameDirectory.set(mkdir(file("runs/client")))
        }
        create("data") {
            clientData()
            gameDirectory.set(mkdir(file("runs/data")))
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
        create("server") {
            server()
            gameDirectory.set(mkdir(file("runs/server")))
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

val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)

listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations.named(variant) {
        attributes {
            attribute(loaderAttribute, "neoforge")
        }
    }
}

sourceSets.configureEach {
    listOf(
        compileClasspathConfigurationName,
        runtimeClasspathConfigurationName,
        getTaskName(null, "jarJar"),
    ).forEach { variant ->
        configurations.named(variant) {
            attributes {
                attribute(loaderAttribute, "neoforge")
            }
        }
    }
}

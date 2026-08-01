import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    `java-library`
    `maven-publish`
}

group = providers.gradleProperty("project_group").get()
version = providers.gradleProperty("project_version").get()

val projectId = providers.gradleProperty("project_id").get()
val projectName = providers.gradleProperty("project_name").get()
val projectOwner = providers.gradleProperty("project_owner").get()
val projectLicense = providers.gradleProperty("project_license").get()
val minecraftVersion = providers.gradleProperty("minecraft_version").get()
val fabricApiVersion = providers.gradleProperty("fabric_api_version").get()
val fabricLoaderVersion = providers.gradleProperty("fabric_loader_version").get()
val neoforgeVersion = providers.gradleProperty("neoforge_version").get()
val neoforgeLoaderMinimum = providers.gradleProperty("neoforge_loader_minimum").get()

base {
    archivesName = "$projectId-${project.name}-$minecraftVersion"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
    withJavadocJar()
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Specification-Title" to projectName,
            "Specification-Vendor" to projectOwner,
            "Specification-Version" to archiveVersion.get(),
            "Implementation-Title" to project.name,
            "Implementation-Version" to archiveVersion.get(),
            "Implementation-Vendor" to projectOwner,
            "Built-On-Minecraft" to minecraftVersion,
        )
    }
}

tasks.named<ProcessResources>("processResources") {
    val expandProps = mapOf(
        "version" to project.version,
        "group" to project.group,
        "minecraft_version" to minecraftVersion,
        "fabric_version" to fabricApiVersion,
        "fabric_loader_version" to fabricLoaderVersion,
        "mod_name" to projectName,
        "mod_author" to projectOwner,
        "mod_id" to projectId,
        "license" to projectLicense,
        "description" to "",
        "neoforge_version" to neoforgeVersion,
        "neoforge_loader_version_range" to "[$neoforgeLoaderMinimum,)",
        "credits" to "",
        "java_version" to "25",
    )

    val jsonExpandProps = expandProps.mapValues { (_, value) ->
        if (value is String) value.replace("\n", "\\\\n") else value
    }

    filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
        expand(expandProps)
    }

    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json")) {
        expand(jsonExpandProps)
    }

    inputs.properties(expandProps)
}

val localMavenURL = providers.environmentVariable("local_maven_url")

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    if (localMavenURL.isPresent) {
        repositories {
            maven {
                url = uri(localMavenURL.get())
            }
        }
    }
}

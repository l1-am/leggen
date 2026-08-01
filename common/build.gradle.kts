import org.gradle.api.attributes.Attribute

plugins {
    id("multiloader-common")
    id("net.neoforged.moddev")
}

val theNeoFormVersion = providers.gradleProperty("neo_form_version").get()

neoForge {
    neoFormVersion = theNeoFormVersion

    val accessTransformer = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (accessTransformer.exists()) {
        accessTransformers.from(accessTransformer.absolutePath)
    }
}

dependencies {
    // Fabric and NeoForge both bundle Fabric Mixin, so it is safe to use it in common.
    // https://github.com/neoforged/NeoForge/blob/26.2.x/gradle.properties#L37
    // https://github.com/FabricMC/fabric-loader/blob/master/gradle.properties#L12
    compileOnly("net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7")
    // Fabric and NeoForge both bundle MixinExtras, so it is safe to use it in common.
    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.3")!!)
}

val commonJava = configurations.consumable("commonJava")
val commonResources = configurations.consumable("commonResources")

artifacts {
    add(commonJava.name, sourceSets.main.get().java.sourceDirectories.singleFile)
    add(commonResources.name, sourceSets.main.get().resources.sourceDirectories.singleFile)
}

val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)

listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations.named(variant) {
        attributes {
            attribute(loaderAttribute, "common")
        }
    }
}

sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName).forEach { variant ->
        configurations.named(variant) {
            attributes {
                attribute(loaderAttribute, "common")
            }
        }
    }
}

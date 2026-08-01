import org.gradle.jvm.tasks.Jar

plugins {
    id("multiloader-common")
}

val commonJava = configurations.register("commonJava") {
    isCanBeResolved = true
}
val commonResources = configurations.register("commonResources") {
    isCanBeResolved = true
}

dependencies {
    compileOnly(project(":common")) {
        attributes {
            attribute(LoaderAttributeHelper.LOADER_ATTRIBUTE, "common")
        }
    }

    commonJava(project(path = ":common", configuration = "commonJava"))
    commonResources(project(path = ":common", configuration = "commonResources"))
}

tasks.compileJava {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.processResources {
    dependsOn(commonResources)
    from(commonResources)
}

tasks.javadoc {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(commonJava)
    from(commonJava)
    dependsOn(commonResources)
    from(commonResources)
}

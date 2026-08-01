import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import org.gradle.api.tasks.SourceSetContainer

object LoaderAttributeHelper {
    val LOADER_ATTRIBUTE = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)

    fun addCommonLoaderAttributes(project: Project, loader: String, vararg additionalConfigurations: NamedDomainObjectProvider<Configuration>) {
        val configurations = project.configurations

        val commonConfigurations = listOf(
            configurations.named("apiElements"),
            configurations.named("runtimeElements"),
            configurations.named("sourcesElements"),
            configurations.named("javadocElements")
        )

        (commonConfigurations + additionalConfigurations)
            .forEach { addLoaderAttribute(it, loader) }

        val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer

        sourceSets.configureEach {
            listOf(
                configurations.named(compileClasspathConfigurationName),
                configurations.named(runtimeClasspathConfigurationName)
            ).forEach { addLoaderAttribute(it, loader) }
        }
    }

    fun addLoaderAttribute(configuration: NamedDomainObjectProvider<Configuration>, loader: String) {
        configuration.configure {
            attributes {
                attribute(LOADER_ATTRIBUTE, loader)
            }
        }
    }
}
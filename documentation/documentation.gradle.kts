import io.mateo.build.GenerateGradleProjectMetadata
import org.gradle.util.GradleVersion

plugins {
    `java-library-conventions`
    alias(libs.plugins.spotless)
}

description = "CXF Codegen documentation"

val pluginApiDocsClasspath by configurations.registering {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    "pluginApiDocsClasspath"(
        project(
            mapOf(
                "path" to ":cxf-codegen-gradle",
                "configuration" to "apiDocs",
            ),
        ),
    )
}

tasks {
    listOf(jar, javadocJar, sourcesJar).forEach { it { enabled = false } }

    val generateGradleProjectMetadata by registering(GenerateGradleProjectMetadata::class) {
        properties.put("cxfVersion", libs.versions.cxf.get())
        properties.put("slf4jVersion", libs.versions.slf4j.get())
    }
    val copyPluginApiDocs by registering(Copy::class) {
        from(pluginApiDocsClasspath) {
            exclude("**/*.jar")
        }
        into(layout.buildDirectory.dir("plugin-api-docs"))
    }
}

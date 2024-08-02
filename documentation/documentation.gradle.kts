import org.gradle.util.GradleVersion

plugins {
    `java-library-conventions`
    alias(libs.plugins.spotless)
    alias(libs.plugins.anotra)
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

antora {
    setOptions(mapOf(
        "attributes" to mapOf(
            "current-gradle-version" to GradleVersion.current().version,
            "plugin-version" to project.version,
            "cxf-version" to libs.versions.cxf.get()
        )
    ))
}

tasks {
    listOf(jar, javadocJar, sourcesJar).forEach { it { enabled = false } }

    val copyPluginApiDocs by registering(Copy::class) {
        from(pluginApiDocsClasspath) {
            exclude("**/*.jar")
        }
        into(layout.buildDirectory.dir("plugin-api-docs"))
    }
    npmInstall {
        outputs.dir(layout.projectDirectory.dir("node_modules"))
    }
}

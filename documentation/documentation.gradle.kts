import io.mateo.build.ProcessExamples
import org.gradle.util.GradleVersion

plugins {
    `java-library-conventions`
    alias(libs.plugins.spotless)
    alias(libs.plugins.gitPublish)
}

description = "CXF Codegen documentation"

val snapshot = rootProject.version.toString().contains("SNAPSHOT")
val docsVersion = if (snapshot) "snapshot" else rootProject.version.toString()
val docsDir = layout.buildDirectory.dir("ghpages-docs")
val replaceCurrentDocs = project.hasProperty("replaceCurrentDocs")

gitPublish {
    repoUri = "https://github.com/ciscoo/cxf-codegen-gradle.git"
    branch = "gh-pages"

    contents {
        from(docsDir)
        into("docs")
    }

    preserve {
        include("**/*")
        if (snapshot) {
            exclude("docs/snapshot/**")
        } else {
            exclude("docs/current/**")
        }
    }
}

val javadoc = configurations.dependencyScope("javadoc")
val javadocClasspath =
    configurations.resolvable("javadocClasspath") {
        extendsFrom(javadoc)
        attributes {
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.JAVADOC))
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        }
    }

dependencies {
    javadoc(projects.cxfCodegenGradle)
}

tasks {
    val extractPluginJavadoc =
        register<Copy>("extractPluginJavadoc") {
            inputs.files(javadocClasspath)
            description = "Extracts the plugin Javadoc."
            from(zipTree(javadocClasspath.map { it.files.single() }))
            into(layout.projectDirectory.dir("public/javadoc"))
        }
    val processExamples =
        register<ProcessExamples>("processExamples") {
            description = "Process all examples from the project."
            source(layout.projectDirectory.dir("src/docs/gradle"))
            include("**/*.gradle.kts", "**/*.gradle")
            outputDirectory = layout.buildDirectory.dir("processed-examples")
        }
    val generateGradleMetadata =
        register("generateGradleMetadata") {
            description = "Generates a metadata file for the documentation."
            val json = layout.buildDirectory.file("gradle-project-metadata.json")
            outputs.file(json)
            doLast {
                json.get().asFile.writeText(
                    """
                    {
                        "version": "$docsVersion",
                        "cxfVersion": "${libs.versions.cxf.get()}",
                        "slf4jVersion": "${libs.versions.slf4j.get()}",
                        "gradleVersion": "${GradleVersion.current().version}"
                    }
                    """.trimIndent(),
                )
            }
        }
    val buildDocs =
        register<Exec>("buildDocs") {
            description = "Builds the documentation for publication."
            inputs.files(extractPluginJavadoc, processExamples, generateGradleMetadata)
            outputs.dir(layout.buildDirectory.dir("dist"))
            outputs.upToDateWhen { false }
            executable = "npm"
            args = listOf("run", "build")
        }
    val previewDocs =
        register<Exec>("previewDocs") {
            description = "Locally preview the production documentation build."
            inputs.files(buildDocs)
            executable = "npm"
            args = listOf("run", "preview")
        }
    val devDocs =
        register<Exec>("devDocs") {
            description = "Start VitePress dev server for documentation development."
            inputs.files(extractPluginJavadoc, processExamples, generateGradleMetadata)
            outputs.dir(layout.buildDirectory.dir("dist"))
            executable = "npm"
            args = listOf("run", "dev")
        }
}

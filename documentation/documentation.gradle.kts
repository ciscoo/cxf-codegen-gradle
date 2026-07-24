import io.mateo.build.ProcessExamples
import org.gradle.util.GradleVersion

plugins {
    `java-library-conventions`
    alias(libs.plugins.spotless)
}

description = "CXF Codegen documentation"

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
            into(layout.buildDirectory.dir("api"))
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
                val docsVersion =
                    if (rootProject.version.toString().endsWith("SNAPSHOT")) {
                        "snapshot"
                    } else {
                        "current"
                    }
                json.get().asFile.writeText(
                    """
                    {
                        "version": "${rootProject.version}",
                        "docsVersion": "$docsVersion"
                        "cxfVersion": "${libs.versions.cxf.get()}",
                        "slf4jVersion": "${libs.versions.slf4j.get()}",
                        "gradleVersion": "${GradleVersion.current().version}"
                    }
                    """.trimIndent(),
                )
            }
        }
    val npmInstall =
        register<Exec>("npmInstall") {
            description = "Installs NPM dependencies."
            outputs.dir(layout.projectDirectory.dir("node_modules"))
            executable = "npm"
            args = listOf("install")
        }
    val prettierFormat =
        register<Exec>("prettierFormat") {
            description = "Runs Prettier."
            inputs.files("**/*.md", "**/*.ts", "package.json")
            executable = "npm"
            args = listOf("run", "format")
        }
    val buildDocs =
        register<Exec>("buildDocs") {
            description = "Builds the documentation for publication."
            inputs.files(extractPluginJavadoc, processExamples, generateGradleMetadata, npmInstall)
            outputs.dir(layout.buildDirectory.dir("dist"))
            outputs.upToDateWhen { false }
            executable = "npm"
            args = listOf("run", "build")
            environment("DOCS_TARGET", providers.environmentVariable("DOCS_TARGET").getOrElse("current"))
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
            inputs.files(extractPluginJavadoc, processExamples, generateGradleMetadata, npmInstall)
            outputs.dir(layout.projectDirectory.dir(".vitepress/cache"))
            outputs.upToDateWhen { false }
            executable = "npm"
            args = listOf("run", "dev")
        }
    register<Copy>("prepareDocsForUpload") {
        description = "Prepares documentation for upload to GitHub Pages"
        into(layout.buildDirectory.dir("gh-pages"))
        from(extractPluginJavadoc) {
            into("api")
        }
        from(buildDocs) {
            into("user-guide")
        }
    }
    clean {
        delete(npmInstall)
        delete(extractPluginJavadoc)
        delete(buildDocs)
        delete(devDocs)
    }
}

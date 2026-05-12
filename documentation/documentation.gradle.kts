import io.mateo.build.ProcessExamples
import org.gradle.util.GradleVersion
import java.io.PrintWriter
import java.nio.file.Files
import kotlin.io.path.bufferedWriter
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readLines

plugins {
    `java-library-conventions`
    alias(libs.plugins.spotless)
    alias(libs.plugins.asciidoctorJvmConvert)
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

val javadoc by configurations.dependencyScope("javadoc")
val javadocClasspath by configurations.resolvable("javadocClasspath") {
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
    val extractPluginJavadoc by registering(Copy::class) {
        from(zipTree(javadocClasspath.files.single()))
        into(layout.projectDirectory.dir("public/javadoc"))
    }
    val processExamples by registering(ProcessExamples::class) {
        source(layout.projectDirectory.dir("src/docs/gradle"))
        include("**/*.gradle.kts", "**/*.gradle")
        outputDirectory = layout.buildDirectory.dir("processed-examples")
    }
    val generateGradleMetadata by registering {
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
    val buildDocs by registering {
        inputs.files(extractPluginJavadoc, processExamples, generateGradleMetadata)
    }
}

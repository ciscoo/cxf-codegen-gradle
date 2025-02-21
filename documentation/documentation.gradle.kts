import org.asciidoctor.gradle.base.AsciidoctorAttributeProvider
import org.asciidoctor.gradle.jvm.AbstractAsciidoctorTask
import org.gradle.kotlin.dsl.withType
import org.gradle.util.GradleVersion

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
        exclude("docs/$docsVersion/**")
        if (replaceCurrentDocs) {
            exclude("docs/current/**")
        }
    }
}

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

    val copyPluginApiDocs by registering(Copy::class) {
        from(pluginApiDocsClasspath) {
            exclude("**/*.jar")
        }
        into(layout.buildDirectory.dir("plugin-api-docs"))
    }

    asciidoctor {
        sources {
            include("**/index.adoc")
        }
        attributes(mapOf(
            "releaseNotesUrl" to "../release-notes/index.html#release-notes"
        ))
    }

    withType<AbstractAsciidoctorTask>().configureEach {
        baseDirFollowsSourceDir()
        attributeProviders.add(
            AsciidoctorAttributeProvider {
                mapOf(
                    "revnumber" to version,
                    "current-gradle-version" to GradleVersion.current().version,
                    "plugin-version" to version,
                    "outdir" to outputDir.absolutePath,
                    "source-highlighter" to "rouge",
                    "tabsize" to "4",
                    "toc" to "left",
                    "numbered" to "true",
                    "toclevels" to "4",
                    "icons" to "font",
                    "sectanchors" to true,
                    "sectnums" to true,
                    "hide-uri-scheme" to true,
                    "idprefix" to "",
                    "idseparator" to "-",
                    "cxf-version" to libs.versions.cxf.get(),
                )
            },
        )
    }

    val prepareDocsForGitHubPages by registering(Copy::class) {
        from(copyPluginApiDocs) {
            into("api")
        }
        from(asciidoctor)
        into(docsDir.map { it.dir(docsVersion) })
        includeEmptyDirs = false
    }

    val createCurrentDocsFolder by registering(Copy::class) {
        from(prepareDocsForGitHubPages)
        into(docsDir.map { it.dir("current") })
        onlyIf { replaceCurrentDocs }
    }

    build {
        dependsOn(createCurrentDocsFolder)
    }

    gitPublishCopy {
        dependsOn(createCurrentDocsFolder)
    }

    gitPublishCommit {
        dependsOn(createCurrentDocsFolder)
    }
}

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
    repoUri.set("https://github.com/ciscoo/cxf-codegen-gradle.git")
    branch.set("gh-pages")

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
        baseDirFollowsSourceDir()
        setOutputDir(layout.buildDirectory.dir("user-guide"))
        attributes(
            mapOf(
                "revnumber" to version,
                "current-gradle-version" to GradleVersion.current().version,
                "plugin-version" to version,
                "outdir" to outputDir.absolutePath,
                "cxf-version" to libs.versions.cxf.get(),
            ),
        )
    }

    val prepareDocsForGitHubPages by registering(Copy::class) {
        from(copyPluginApiDocs) {
            into("api")
        }
        from(asciidoctor) {
            into("user-guide")
        }
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

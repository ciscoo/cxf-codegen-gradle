import org.gradle.util.GradleVersion

plugins {
    `java-library-conventions`
    id("com.diffplug.spotless")
    id("org.asciidoctor.jvm.convert")
    id("org.ajoberstar.git-publish")
}

description = "CXF Codegen documentation"

val snapshot = rootProject.version.toString().contains("SNAPSHOT")
val docsVersion = if (snapshot) "snapshot" else rootProject.version
val docsDir = file("$buildDir/ghpages-docs")
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

tasks {
    val copyJavadoc by registering(Copy::class) {
        from("$rootDir/cxf-codegen-gradle/build/docs/javadoc")
        into("$buildDir/docs/api")
    }

    asciidoctor {
        dependsOn(copyJavadoc)
        setOutputDir("$buildDir/docs/user-guide")
        attributes(mapOf(
            "revnumber" to version,
            "current-gradle-version" to GradleVersion.current().version,
            "outdir" to outputDir.absolutePath,
            "source-highlighter" to "rouge",
            "tabsize" to "4",
            "toc" to "left",
            "icons" to "font",
            "sectanchors" to true,
            "idprefix" to "",
            "idseparator" to "-"
        ))
    }

    build {
        dependsOn(asciidoctor)
    }

    val prepareDocsForGitHubPages by registering(Copy::class) {
        dependsOn(asciidoctor)
        outputs.dir(docsDir)
        from("$buildDir/checksum") {
            include("published-checksum.txt")
        }
        from("$buildDir/docs") {
            include("user-guide/**", "api/**")
        }
        into("$docsDir/$docsVersion")
        includeEmptyDirs = false
    }

    val createCurrentDocsFolder by registering(Copy::class) {
        dependsOn(prepareDocsForGitHubPages)
        outputs.dir("$docsDir/current")
        onlyIf { replaceCurrentDocs }
        from("$docsDir/$docsVersion")
        into("$docsDir/current")
    }

    gitPublishCommit {
        dependsOn(prepareDocsForGitHubPages, createCurrentDocsFolder)
    }
}

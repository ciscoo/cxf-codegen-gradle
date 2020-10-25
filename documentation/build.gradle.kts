import org.gradle.util.GradleVersion

plugins {
    `java-library-conventions`
    id("com.diffplug.spotless")
    id("org.asciidoctor.jvm.convert")
}

description = "CXF Codegen Gradle documentation"

tasks {
    val copyJavadoc by registering(Copy::class) {
        from("$rootDir/plugin/build/docs/javadoc")
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
}

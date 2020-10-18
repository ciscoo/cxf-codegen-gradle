import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.nio.charset.StandardCharsets
import java.util.Locale

plugins {
    `java-library`
}

configurations {
    val internal by registering {
        isVisible = false
        isCanBeConsumed = false
        isCanBeResolved = false
    }
    matching { name.endsWith("Classpath") }.configureEach {
        extendsFrom(internal.get())
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.17.2")
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(15))
    }
}

tasks {
    withType<Jar>().configureEach {
        from(rootDir) {
            include("$rootDir/LICENSE.txt")
            into("META-INF")
        }
        manifest {
            attributes["Automatic-Module-Name"] = project.name.replace("-", ".")
            attributes["Created-By"] = "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")})"
            attributes["Implementation-Title"] = project.description
            attributes["Implementation-Version"] = project.version.toString()
            attributes["Specification-Title"] = project.description
            attributes["Specification-Version"] = project.version.toString()
        }
    }

    withType<JavaCompile>().configureEach {
        options.encoding = StandardCharsets.UTF_8.toString()
    }

    withType<Test>().configureEach {
        useJUnitPlatform {
            includeEngines("junit-jupiter")
        }
        testLogging {
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.STANDARD_ERROR,
                TestLogEvent.SKIPPED
            )
        }
        javaLauncher.set(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(15))
        })
    }

    compileJava {
        options.release.set(JavaVersion.VERSION_1_8.majorVersion.toInt())
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
    }

    compileTestJava {
        options.release.set(JavaVersion.VERSION_15.majorVersion.toInt())
        options.compilerArgs.addAll(listOf("-Xlint", "-Xlint:-overrides", "-Werror", "-parameters"))
    }

    javadoc {
        options {
            memberLevel = JavadocMemberLevel.PROTECTED
            header = project.name
            encoding = StandardCharsets.UTF_8.toString()
            locale = Locale.ENGLISH.language
            this as StandardJavadocDocletOptions
            links = listOf(
                "https://docs.oracle.com/javase/8/docs/api/",
                "https://docs.gradle.org/current/javadoc/"
            )
            addBooleanOption("Xdoclint:html,syntax", true)
            addBooleanOption("html5", true)
            use()
            noTimestamp()
            // Suppress warnings due to cross-module @see and @link references.
            logging.captureStandardError(LogLevel.INFO)
            logging.captureStandardOutput(LogLevel.INFO)
        }
        javadocTool.set(javaToolchains.javadocToolFor {
            languageVersion.set(JavaLanguageVersion.of(15))
        })
    }
}

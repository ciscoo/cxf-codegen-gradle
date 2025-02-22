import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.nio.charset.StandardCharsets
import java.util.Locale

plugins {
    `java-library`
    id("code-style-conventions")
}

val libsCatalog = versionCatalogs.named("libs")

dependencies {
    libsCatalog.findLibrary("junit-bom").ifPresent {
        testImplementation(platform(it))
    }
    libsCatalog.findLibrary("junit-jupiter").ifPresent {
        testImplementation(it)
    }
    libsCatalog.findLibrary("junit-pioneer").ifPresent {
        testImplementation(it)
    }
    libsCatalog.findLibrary("apache-lang3").ifPresent {
        testImplementation(it)
    }
    libsCatalog.findLibrary("assertj-core").ifPresent {
        testImplementation(it)
    }
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

testing {
    suites {
        withType(JvmTestSuite::class) {
            libsCatalog.findVersion("junit").ifPresent {
                useJUnitJupiter(it.requiredVersion)
            }
            targets.configureEach {
                testTask.configure {
                    testLogging {
                        events = setOf(
                            TestLogEvent.FAILED,
                            TestLogEvent.STANDARD_ERROR,
                            TestLogEvent.SKIPPED
                        )
                    }
                }
            }
        }
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

    compileJava {
        options.release = JavaVersion.VERSION_1_8.majorVersion.toInt()
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
    }

    compileTestJava {
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
                "https://docs.oracle.com/en/java/javase/11/docs/api/",
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
    }
}

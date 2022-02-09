import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.nio.charset.StandardCharsets
import java.util.Locale

plugins {
    `java-library`
    id("code-style-conventions")
}

configurations {
    val internal by registering {
        isVisible = false
        isCanBeConsumed = false
        isCanBeResolved = false
    }
    matching { it.name.endsWith("Classpath") }.configureEach {
        extendsFrom(internal.get())
    }
}

dependencies {
    "internal"(platform(project(":dependencies")))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

testing {
    suites {
        withType(JvmTestSuite::class) {
            targets.configureEach {
                testTask.configure {
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
                        languageVersion.set(JavaLanguageVersion.of(11))
                    })
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
        options.release.set(JavaVersion.VERSION_1_8.majorVersion.toInt())
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
    }

    compileTestJava {
        options.release.set(JavaVersion.VERSION_11.majorVersion.toInt())
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
        javadocTool.set(javaToolchains.javadocToolFor {
            languageVersion.set(JavaLanguageVersion.of(11))
        })
    }
}

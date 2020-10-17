import java.nio.charset.StandardCharsets
import java.util.Locale
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.12.0"
    id("com.diffplug.spotless") version "5.6.1"
}

repositories {
    mavenCentral()
}

group = "io.mateo"
description = "CXF Codegen Gradle"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.17.2")
}

gradlePlugin {
    plugins {
        create("cxfCodegen") {
            id = "io.mateo.cxf-codegen"
            displayName = "CXF Codegen Gradle"
            description = "Plugin to generate Java artifacts from WSDL"
            implementationClass = "io.mateo.cxf.codegen.CxfCodegenPlugin"
        }
    }
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(15))
    }
}

spotless {
    java {
        eclipse()
        licenseHeaderFile(rootProject.file("src/spotless/apache-license-2.0.java"), "(package|import|open|module)")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        ktlint()
        indentWithSpaces(4)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

gradlePlugin.testSourceSets(functionalTestSourceSet)

configurations {
    "functionalTestImplementation" {
        extendsFrom(testImplementation.get())
    }
}

tasks {
    val functionalTest by registering(Test::class) {
        description = "Runs the functional tests."
        testClassesDirs = functionalTestSourceSet.output.classesDirs
        classpath = functionalTestSourceSet.runtimeClasspath
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }
    check {
        dependsOn(functionalTest)
    }
    publishPlugins {
        dependsOn(build)
    }
    withType<JavaCompile>().configureEach {
        options.encoding = StandardCharsets.UTF_8.toString()
    }

    // https://docs.oracle.com/en/java/javase/15/docs/specs/man/javac.html
    compileJava {
        options.release.set(JavaVersion.VERSION_1_8.majorVersion.toInt())
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
    }
    compileTestJava {
        options.release.set(JavaVersion.VERSION_15.majorVersion.toInt())
        options.compilerArgs.addAll(listOf("-Xlint", "-Xlint:-overrides", "-Werror", "-parameters"))
    }

    withType<Jar>().configureEach {
        from(rootDir) {
            include("LICENSE.txt")
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
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
}

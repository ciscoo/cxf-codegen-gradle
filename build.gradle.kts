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
description = "Plugin to generate Java artifacts from WSDL"

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
            description = project.description
            implementationClass = "io.mateo.cxf.codegen.CxfCodegenPlugin"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
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
        with(options) {
            encoding = StandardCharsets.UTF_8.toString()
            release.set(Integer.parseInt(JavaVersion.VERSION_1_8.majorVersion))
        }
    }

    // https://docs.oracle.com/en/java/javase/15/docs/specs/man/javac.html
    compileJava {
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
    }
    compileTestJava {
        options.compilerArgs.addAll(listOf("-Xlint", "-Xlint:-overrides", "-Werror", "-parameters"))
    }

    withType<Jar>().configureEach {
        manifest {
            attributes["Automatic-Module-Name"] = project.name.replace("-", ".")
            attributes["Implementation-Title"] = project.description
            attributes["Implementation-Version"] = project.version.toString()
        }
    }
    withType<Javadoc>().configureEach {
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
    }
    withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            events = setOf(
                    TestLogEvent.FAILED,
                    TestLogEvent.STANDARD_ERROR,
                    TestLogEvent.SKIPPED
            )
        }
    }
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
}

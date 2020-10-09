import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.12.0"
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("com.diffplug.spotless") version "5.6.1"
}

repositories {
    mavenCentral()
}

group = "io.mateo"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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
    sourceCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

spotless {
    kotlin {
        ktlint()
        licenseHeaderFile(rootProject.file("src/spotless/apache-license-2.0.kt"), "(package|import|open|module)")
        indentWithSpaces(4)
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

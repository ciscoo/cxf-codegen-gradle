plugins {
    `java-gradle-plugin`
    `java-library-conventions`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.12.0"
    id("com.diffplug.spotless") version "5.6.1"
}

repositories {
    mavenCentral()
}

group = "io.mateo"
description = "CXF Codegen Gradle"

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

dependencies {
    "functionalTestImplementation"("commons-io:commons-io:2.8.0")
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
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
}

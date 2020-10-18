plugins {
    `java-gradle-plugin`
    `java-library-conventions`
    `maven-publish`
    id("com.gradle.plugin-publish")
    id("com.diffplug.spotless")
}

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

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

gradlePlugin.testSourceSets(functionalTestSourceSet)

configurations {
    "functionalTestImplementation" {
        extendsFrom(testImplementation.get())
    }
}

dependencies {
    "functionalTestImplementation"("commons-io:commons-io")
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
}

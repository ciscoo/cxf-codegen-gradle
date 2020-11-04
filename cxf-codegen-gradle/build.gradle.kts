plugins {
    `java-gradle-plugin`
    `java-library-conventions`
    `maven-publish`
    signing
    id("com.gradle.plugin-publish")
    id("com.diffplug.spotless")
}

description = "CXF Codegen"

gradlePlugin {
    plugins {
        create("cxfCodegen") {
            id = "io.mateo.cxf-codegen"
            displayName = "CXF Codegen"
            description = "Plugin to generate code sources from WSDL."
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
    withType<Jar>().configureEach {
        manifest.attributes["Automatic-Module-Name"] = "io.mateo.cxf.codegen"
    }
    javadoc {
        options {
            header = "CXF Codegen"
            windowTitle = "CXF Codegen Gradle Plugin $version API"
            this as StandardJavadocDocletOptions
            docTitle = "CXF Codegen Gradle Plugin $version API"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "central"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            credentials {
                username = System.getenv("OSSRH_USER_TOKEN")
                password = System.getenv("OSSRH_PWD_TOKEN")
            }
        }
    }
}

signing {
    sign(publishing.publications)
}

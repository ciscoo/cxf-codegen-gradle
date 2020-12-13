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
            name = "sonatype"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            credentials(PasswordCredentials::class)
        }
    }
    publications.containerWithType(MavenPublication::class).configureEach {
        pom {
            url.set("https://github.com/ciscoo/cxf-codegen-gradle")
            licenses {
                name.set("Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
            developers {
                developer {
                    name.set("Francisco Mateo")
                    email.set("cisco21c@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:github.com/ciscoo/cxf-codegen-gradle.git")
                developerConnection.set("scm:git:ssh://github.com/ciscoo/cxf-codegen-gradle.git")
                url.set("https://github.com/ciscoo/cxf-codegen-gradle")
            }
            issueManagement {
                system.set("GitHub")
                url.set("https://github.com/ciscoo/cxf-codegen-gradle/issues")
            }
        }
    }
}

val isSnapshot = project.version.toString().endsWith("SNAPSHOT")
val isCIEnvironment = System.getenv("CI")?.toBoolean() ?: false

signing {
    sign(publishing.publications)
    isRequired = !(isSnapshot || isCIEnvironment)
}

afterEvaluate {
    publishing {
        publications.named<MavenPublication>("pluginMaven") {
            pom {
                name.set("CXF Codegen Gradle Plugin")
                description.set("Plugin to generate code sources from WSDL.")
            }
        }
        publications.named<MavenPublication>("cxfCodegenPluginMarkerMaven") {
            pom {
                name.set("CXF Codegen Gradle Plugin Marker")
                description.set("CXF Codegen Gradle plugin marker artifact.")
            }
        }
    }
}

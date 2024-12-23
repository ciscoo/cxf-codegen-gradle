plugins {
    `java-library-conventions`
    signing
    jacoco
    `maven-publish`
    `java-gradle-plugin`
}

description = "CXF Codegen"

gradlePlugin {
    website.set("https://ciscoo.github.io/cxf-codegen-gradle/docs/current/user-guide/")
    vcsUrl.set("https://github.com/ciscoo/cxf-codegen-gradle")
    plugins {
        create("cxfCodegen") {
            id = "io.mateo.cxf-codegen"
            displayName = "CXF Codegen"
            description =
                """
                Gradle plugin to generate code sources from WSDL.
                The next major version and subsequent versions will only be published to the Maven Central Repository.
                """.trimIndent()
            implementationClass = "io.mateo.cxf.codegen.CxfCodegenPlugin"
            tags.set(listOf("cxf", "wsdl2java"))
        }
    }
}

val generateVersionAccessor =
    tasks.register("generateVersionAccessor", io.mateo.build.GenerateVersionAccessor::class) {
        cxfVersion.set(libs.versions.cxf)
        slf4jVersion.set(libs.versions.slf4j)
    }

sourceSets.main {
    java.srcDir(generateVersionAccessor)
}

spotless {
    java {
        targetExclude(
            fileTree(generateVersionAccessor.flatMap { provider { it.outputDirectory.get() } }) {
                include("**/*.java")
            },
        )
    }
}

testing {
    suites {
        register("functionalTest", JvmTestSuite::class) {
            dependencies {
                implementation(platform(libs.junitBom))
                implementation(libs.junitJupiter)
                implementation(project())
                implementation(libs.assertj)
            }
            targets.configureEach {
                testTask.configure {
                    testLogging {
                        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                    }
                }
            }
        }
    }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks {
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
    test {
        finalizedBy(jacocoTestReport)
    }
    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required.set(true)
        }
    }
}
val isSnapshot = project.version.toString().endsWith("SNAPSHOT")

configurations.register("apiDocs") {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add("apiDocs", tasks.javadoc)
}

publishing {
    repositories {
        maven {
            name = "sonatypeSnapshots"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            credentials(PasswordCredentials::class)
        }
    }
    publications.containerWithType(MavenPublication::class).configureEach {
        pom {
            url.set("https://github.com/ciscoo/cxf-codegen-gradle")
            licenses {
                license {
                    name.set("Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
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

val isCIEnvironment = System.getenv("CI")?.toBoolean() ?: false

signing {
    isRequired = !(isSnapshot || isCIEnvironment)
    sign(publishing.publications)
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
    tasks {
        named("publishCxfCodegenPluginMarkerMavenPublicationToSonatypeSnapshotsRepository") {
            onlyIf("snapshot") {
                isSnapshot
            }
        }
        named("publishPluginMavenPublicationToSonatypeSnapshotsRepository") {
            onlyIf("snapshot") {
                isSnapshot
            }
        }
    }
}

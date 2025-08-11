plugins {
    `java-library-conventions`
    signing
    jacoco
    `maven-publish`
    `java-gradle-plugin`
}

description = "CXF Codegen"

gradlePlugin {
    website = "https://ciscoo.github.io/cxf-codegen-gradle/docs/current/user-guide/"
    vcsUrl = "https://github.com/ciscoo/cxf-codegen-gradle"
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
            tags = listOf("cxf", "wsdl2java")
        }
    }
}

val generateVersionAccessor =
    tasks.register("generateVersionAccessor", io.mateo.build.GenerateVersionAccessor::class) {
        cxfVersion = libs.versions.cxf
        slf4jVersion = libs.versions.slf4j
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
                implementation(platform(libs.junit.bom))
                implementation(libs.junit.jupiter)
                implementation(project())
                implementation(libs.assertj.core)
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

val stagingRepoDir = layout.buildDirectory.dir("staging-repo")

tasks {
    register<Delete>("cleanStagingRepo") {
        description = "Deletes only the staging repository directory."
        delete(stagingRepoDir)
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
        source = sourceSets.main.map { it.allJava.filter { !it.canonicalPath.contains("internal") }.asFileTree }.get()
    }
    test {
        finalizedBy(jacocoTestReport)
    }
    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required = true
        }
    }
    register("publishAllToStagingRepository") {
        dependsOn(withType<PublishToMavenRepository>().named { it.endsWith("ToStagingRepository") })
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
            name = "mavenCentralSnapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
            credentials(PasswordCredentials::class)
        }
        maven {
            name = "staging"
            url = uri(stagingRepoDir.map { it.asFile })
        }
    }
    publications.containerWithType(MavenPublication::class).configureEach {
        pom {
            url = "https://github.com/ciscoo/cxf-codegen-gradle"
            licenses {
                license {
                    name = "Apache License, Version 2.0"
                    url = "https://www.apache.org/licenses/LICENSE-2.0"
                }
            }
            developers {
                developer {
                    name = "Francisco Mateo"
                    email = "cisco21c@gmail.com"
                }
            }
            scm {
                connection = "scm:git:github.com/ciscoo/cxf-codegen-gradle.git"
                developerConnection = "scm:git:ssh://github.com/ciscoo/cxf-codegen-gradle.git"
                url = "https://github.com/ciscoo/cxf-codegen-gradle"
            }
            issueManagement {
                system = "GitHub"
                url = "https://github.com/ciscoo/cxf-codegen-gradle/issues"
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
                name = "CXF Codegen Gradle Plugin"
                description = "Plugin to generate code sources from WSDL."
            }
        }
        publications.named<MavenPublication>("cxfCodegenPluginMarkerMaven") {
            pom {
                name = "CXF Codegen Gradle Plugin Marker"
                description = "CXF Codegen Gradle plugin marker artifact."
            }
        }
    }
    tasks {
        named("publishCxfCodegenPluginMarkerMavenPublicationToMavenCentralSnapshotsRepository") {
            onlyIf("snapshot") {
                isSnapshot
            }
        }
        named("publishPluginMavenPublicationToMavenCentralSnapshotsRepository") {
            onlyIf("snapshot") {
                isSnapshot
            }
        }
    }
}

plugins {
    `java-library-conventions`
    jacoco
    `java-gradle-plugin`
    `maven-publish`
    `jreleaser-conventions`
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

dependencies {
    compileOnly(libs.cxf.toolsWsdltoCore)
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
}

configurations.register("apiDocs") {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add("apiDocs", tasks.javadoc)
}

publishing {
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
}

import org.jreleaser.model.Active
import org.jreleaser.model.api.deploy.maven.MavenCentralMavenDeployer.Stage

plugins {
    `maven-publish`
    signing
    id("org.jreleaser")
}

val stagingRepoDir by extra {
    layout.buildDirectory.file("staging-repo")
}

tasks {
    register<Delete>("cleanStagingRepo") {
        description = "Deletes only the staging repository directory."
        delete(stagingRepoDir)
    }
    register("publishAllToStagingRepoRepository") {
        dependsOn(withType<PublishToMavenRepository>().named { it.endsWith("ToStagingRepoRepository") })
    }
}

val mavenCentralUsername = providers.gradleProperty("mavenCentralUsername")
val mavenCentralPassword = providers.gradleProperty("mavenCentralPassword")

publishing {
    repositories {
        maven {
            name = "mavenCentralSnapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
            credentials {
                username = mavenCentralUsername.orNull
                password = mavenCentralPassword.orNull
            }
        }
        maven {
            name = "stagingRepo"
            url = uri(stagingRepoDir)
        }
    }
}

val isCIEnvironment = System.getenv("CI")?.toBoolean() ?: false
val isSnapshot = project.version.toString().endsWith("SNAPSHOT")

signing {
    isRequired = !(isSnapshot || isCIEnvironment)
    sign(publishing.publications)
}

jreleaser {
    deploy {
        maven {
            mavenCentral {
                register("release") {
                    active = Active.RELEASE
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(stagingRepoDir.get())
                    username = mavenCentralUsername
                    password = mavenCentralPassword
                    applyMavenCentralRules = false
                    sourceJar = false
                    javadocJar = false
                    sign = false
                    checksums = false
                    verifyPom = false
                    namespace = "io.mateo"
                    stage = Stage.UPLOAD
                }
            }
        }
    }
}

plugins {
    `code-style-conventions`
    id("org.sonarqube") version "3.3"
}

defaultTasks("build")

allprojects {
    group = "io.mateo"
}

sonarqube {
    properties {
        property("sonar.projectKey", "ciscoo_cxf-codegen-gradle")
        property("sonar.organization", "mateoio")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

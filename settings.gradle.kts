pluginManagement {
    plugins {
        id("com.gradle.plugin-publish") version "0.14.0"
        id("org.asciidoctor.jvm.convert") version "3.3.2"
        id("org.ajoberstar.git-publish") version "4.1.0"
    }
}

rootProject.name = "cxf-codegen-gradle"

include("dependencies")
include("documentation")
include("cxf-codegen-gradle")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

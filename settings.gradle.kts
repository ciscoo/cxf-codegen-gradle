pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.gradle.plugin-publish") version "1.1.0"
        id("org.asciidoctor.jvm.convert") version "3.3.2"
        id("org.ajoberstar.git-publish") version "4.2.1"
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

startParameter.showStacktrace = ShowStacktrace.ALWAYS_FULL
startParameter.warningMode = WarningMode.Summary

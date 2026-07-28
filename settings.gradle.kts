pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("build-logic-settings")
}

plugins {
    id("publishing-conventions")
}

rootProject.name = "cxf-codegen-gradle-build"

include("documentation")
include("cxf-codegen-gradle")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

fun ProjectDescriptor.ensureBuildFileExists() {
    buildFileName = "$name.gradle.kts"
    require(buildFile.isFile) { "$buildFile must exist" }
}

fun requireBuildFileName(projectDescriptor: ProjectDescriptor) {
    projectDescriptor.ensureBuildFileExists()
    projectDescriptor.children.forEach {
        requireBuildFileName(it)
    }
}

rootProject.children.forEach {
    it.ensureBuildFileExists()
    requireBuildFileName(it)
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

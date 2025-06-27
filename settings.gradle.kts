pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "cxf-codegen-gradle"

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

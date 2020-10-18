pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "5.6.1"
        id("com.gradle.plugin-publish") version "0.12.0"
    }
}

rootProject.name = "cxf-codegen-gradle"

include("dependencies")
include("plugin")

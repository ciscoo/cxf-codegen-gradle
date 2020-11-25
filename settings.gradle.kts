pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "5.8.2"
        id("com.gradle.plugin-publish") version "0.12.0"
        id("org.asciidoctor.jvm.convert") version "3.2.0"
    }
}

rootProject.name = "cxf-codegen-gradle"

include("dependencies")
include("documentation")
include("cxf-codegen-gradle")

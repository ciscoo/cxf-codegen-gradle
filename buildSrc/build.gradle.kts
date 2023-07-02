plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.nexusPublish)
    implementation(libs.gradle.spotless)
    implementation(libs.gradle.springJavaFormat)
    implementation(libs.javapoet)
}

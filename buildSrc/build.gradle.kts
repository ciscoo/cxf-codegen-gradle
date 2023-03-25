plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.gradle.spotless)
    implementation(libs.gradle.springJavaFormat)
    implementation(libs.javapoet)
}

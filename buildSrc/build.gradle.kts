plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.10.0")
    implementation("io.spring.javaformat:spring-javaformat-gradle-plugin:0.0.34")
    implementation("com.squareup:javapoet:1.13.0")
}

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.17.0")
    implementation("io.spring.javaformat:spring-javaformat-gradle-plugin:0.0.38")
    implementation("com.squareup:javapoet:1.13.0")
}

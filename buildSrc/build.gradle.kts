plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.9.0")
    implementation("io.spring.javaformat:spring-javaformat-gradle-plugin:0.0.34")
}

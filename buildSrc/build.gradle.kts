plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:5.11.0")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

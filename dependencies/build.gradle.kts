plugins {
    `java-platform`
    id("com.diffplug.spotless")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform(libs.junitBom))
    api(libs.assertj)
}

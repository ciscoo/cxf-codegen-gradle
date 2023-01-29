plugins {
    `java-platform`
    id("com.diffplug.spotless")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.junit:junit-bom:5.9.1"))
    api("org.assertj:assertj-core:3.23.1")
}

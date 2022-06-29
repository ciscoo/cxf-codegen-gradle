plugins {
    `java-platform`
    id("com.diffplug.spotless")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.junit:junit-bom:5.9.0-M1"))
    api("org.assertj:assertj-core:3.23.1")
    api("commons-io:commons-io:2.11.0")
}

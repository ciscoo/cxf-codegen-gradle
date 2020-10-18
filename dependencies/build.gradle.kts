plugins {
    `java-platform`
    id("com.diffplug.spotless")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.junit:junit-bom:5.7.0"))
    api("org.assertj:assertj-core:3.17.2")
    api("commons-io:commons-io:2.8.0")
}

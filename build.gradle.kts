plugins {
    id("com.diffplug.spotless")
}

allprojects {
    group = "io.mateo"

    repositories {
        mavenCentral()
    }

    pluginManager.withPlugin("com.diffplug.spotless") {
        spotless {
            kotlinGradle {
                ktlint()
                trimTrailingWhitespace()
                endWithNewline()
            }
        }
    }
}

subprojects {
    pluginManager.withPlugin("com.diffplug.spotless") {
        spotless {
            pluginManager.withPlugin("java") {
                java {
                    eclipse()
                    licenseHeaderFile(rootProject.file("src/spotless/apache-license-2.0.java"), "(package|import|open|module)")
                    removeUnusedImports()
                    trimTrailingWhitespace()
                    endWithNewline()
                }
            }
        }
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

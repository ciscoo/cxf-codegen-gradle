plugins {
    id("com.diffplug.spotless")
}

allprojects {
    group = "io.mateo"

    pluginManager.apply("com.diffplug.spotless")

    repositories {
        mavenCentral()
    }

    spotless {
        kotlinGradle {
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

subprojects {
    spotless {
        java {
            eclipse()
            licenseHeaderFile(rootProject.file("src/spotless/apache-license-2.0.java"), "(package|import|open|module)")
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

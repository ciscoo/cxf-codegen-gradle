plugins {
    id("com.diffplug.spotless")
}

spotless {
    kotlinGradle {
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

pluginManager.withPlugin("java") {
    spotless {
        java {
            licenseHeaderFile(rootProject.file("src/spotless/apache-license-2.0.java"), "(package|import|open|module)")
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
            palantirJavaFormat()
        }
    }
}

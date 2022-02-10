plugins {
    id("com.diffplug.spotless")
    id("io.spring.javaformat")
}

spotless {
    kotlinGradle {
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }
    pluginManager.withPlugin("java") {
        java {
            licenseHeaderFile(rootProject.file("src/spotless/apache-license-2.0.java"), "(package|import|open|module)")
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

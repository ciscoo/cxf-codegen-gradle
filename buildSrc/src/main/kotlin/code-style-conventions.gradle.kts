plugins {
    id("com.diffplug.spotless")
}

spotless {
    kotlinGradle {
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }
    pluginManager.withPlugin("java") {
        java {
            eclipse().configFile(file("$rootDir/src/eclipse/eclipse-formatter-settings.xml"))
            importOrderFile(file("$rootDir/src/eclipse/eclipse.importorder"))
            licenseHeaderFile(rootProject.file("src/spotless/apache-license-2.0.java"), "(package|import|open|module)")
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

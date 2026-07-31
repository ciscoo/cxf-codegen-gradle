plugins {
    id("com.gradleup.nmcp.settings")
}

nmcpSettings {
    centralPortal {
        username = providers.gradleProperty("mavenCentralUsername")
        password = providers.gradleProperty("mavenCentralPassword")
        publishingType = "USER_MANAGED"
        publishingTimeout = validationTimeout
    }
}

gradle.lifecycle.beforeProject {
    pluginManager.apply("signing")

    extensions.configure(SigningExtension::class) {
        val isCIEnvironment = System.getenv("CI")?.toBoolean() ?: false
        val isSnapshot = project.version.toString().endsWith("SNAPSHOT")
        isRequired = !(isSnapshot || isCIEnvironment)
    }

   pluginManager.withPlugin("maven-publish") {
       extensions.configure(SigningExtension::class) {
           sign(extensions.getByType<PublishingExtension>().publications)
       }
   }
}

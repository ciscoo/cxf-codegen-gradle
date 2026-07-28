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

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugins.nmcp.settings.markerCoordinates)
}

// https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_markers
val Provider<PluginDependency>.markerCoordinates: Provider<String>
    get() = map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }

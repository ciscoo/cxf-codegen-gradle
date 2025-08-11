plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.spotless)
    implementation(libs.javapoet)
}

// TODO: https://youtrack.jetbrains.com/issue/KT-63165
@Suppress("UNCHECKED_CAST")
val kotlinCKGPCEClass: Class<DefaultTask> = Class
    .forName("org.jetbrains.kotlin.gradle.plugin.diagnostics.CheckKotlinGradlePluginConfigurationErrors")
    as Class<DefaultTask>

tasks.withType(kotlinCKGPCEClass).configureEach {
    val getBuildServiceProvider: java.lang.reflect.Method = Class
        .forName("org.jetbrains.kotlin.gradle.plugin.diagnostics.KotlinToolingDiagnosticsCollectorKt")
        .getDeclaredMethod("getKotlinToolingDiagnosticsCollectorProvider", Project::class.java)

    @Suppress("UNCHECKED_CAST")
    val buildServiceProvider = getBuildServiceProvider.invoke(null, project) as Provider<BuildService<*>>
    usesService(buildServiceProvider)
}

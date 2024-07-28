<script setup>
const gradleVersion = __GRADLE_VERSION__
const pluginVersion = __PLUGIN_VERSION__
</script>

# Overview

The goal of this document is to provide reference documentation for developers utilizing the CXF Codegen Gradle plugin.

## What is CXF Codegen

CXF Codegen is a Gradle plugin port of the [Maven plugin](https://cxf.apache.org/docs/maven-cxf-codegen-plugin-wsdl-to-java.html).
The Gradle plugin offers an API similar to the one offered by the Maven plugin. The API is not a 1:1 port and leans heavily
on Gradle idioms and conventions.

### Getting Started

To get started with the plugin it needs to be applied to your project.

The plugin is published to [Maven Central](https://central.sonatype.com) and can be applied using the plugins DSL block:

::: code-group
<<< @/src/docs/gradle/overview/plugin-application.gradle.kts{kotlin-vue}[Kotlin]
<<< @/src/docs/gradle/overview/plugin-application.gradle{groovy-vue}[Groovy]
:::

## Supported Gradle Versions

The CXF Codegen Gradle plugin supports the following Gradle versions:

| Gradle |
|--------|
| 8.4    |
| 8.5    |
| 8.6    |
| 8.7    |
| 8.8    |

The plugin is fully tested against the above versions to ensure compatibility. View the source of the
[functional tests](https://github.com/ciscoo/cxf-codegen-gradle/tree/master/cxf-codegen-gradle/src/functionalTest) for more details.

Gradle's [configuration cache](https://docs.gradle.org/current/userguide/configuration_cache.html) is supported.

> [!WARNING]
> All examples are written for _and_ are tested for Gradle {{ gradleVersion }}. Depending on your Gradle version,
> you may need to adapt the example to a syntax that is compatible with your Gradle version.

### Support Policy

Each major version of this plugin targets a specific Gradle major version. For example, 2.x.x targets Gradle 8.x and
3.x.x targets Gradle 9.x and so on.

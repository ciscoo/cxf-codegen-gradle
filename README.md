# CXF Codegen [![Build Status](https://github.com/ciscoo/cxf-codegen-gradle/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/ciscoo/cxf-codegen-gradle/actions/workflows/build.yml)

CXF Codegen is a Gradle plugin port of the
[CXF Codegen Maven plugin](https://cxf.apache.org/docs/maven-cxf-codegen-plugin-wsdl-to-java.html).

## Getting Started

The plugin is [published to Maven Central](https://central.sonatype.com/) and can be applied
using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block) block:

> [!NOTE]
> The plugin was previously published to the Gradle Plugin portal, but switched to publishing to Maven Central
> primarily to track download statistics which the Gradle Plugin portal [does not provide](https://github.com/gradle/plugin-portal-requests/issues/2).

<details open>
<summary>Kotlin</summary>

```kotlin
plugins {
    java
    id("io.mateo.cxf-codegen") version "3.0.0"
}
```

</details>

<details>
<summary>Groovy</summary>

```groovy
plugins {
    id "java"
    id "io.mateo.cxf-codegen" version "3.0.0"
}
```

</details>

> [!NOTE]
> The Gradle plugin portal proxies Maven Central, so you should not need to configure Maven Central as a plugin repository.

## Documentation

- [User Guide](https://ciscoo.github.io/cxf-codegen-gradle/docs/current/user-guide/)
- [Javadoc](https://ciscoo.github.io/cxf-codegen-gradle/docs/current/api/index.html)

## Snapshots

Snapshots of the next development version are published to the Maven Central Portal Snapshots repository.

Documentation for snapshots:

- [User Guide](https://ciscoo.github.io/cxf-codegen-gradle/docs/snapshot/user-guide/)
- [Javadoc](https://ciscoo.github.io/cxf-codegen-gradle/docs/snapshot/api/index.html)

Snapshots are published for every commit to the `master` branch.

<details open>
<summary>Kotlin</summary>

```kotlin
// build.gradle.kts

plugins {
    java
    id("io.mateo.cxf-codegen") version "3.0.1-SNAPSHOT"
}

// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}
```

</details>

<details>
<summary>Groovy</summary>

```groovy
// build.gradle
plugins {
    id "java"
    id "io.mateo.cxf-codegen" version "3.0.1-SNAPSHOT"
}

// settings.gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}
```

</details>

## Building from Source

You will need Java 17 to build CXF Codegen.

> [!TIP]
> A compatible Java installation will automatically be downloaded by Gradle if you do not have one.
> See [Toolchains for JVM projects](https://docs.gradle.org/current/userguide/toolchains.html) for more details.

The plugin can be built and published to your local Maven cache using the
[Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

```bash
./gradlew nmcpPublishAggregationToMavenLocal
```

This will build and publish it to your local Maven cache. It won't run any of
the tests. If you want to build everything, use the `build` task:

```bash
./gradlew build
```

<script setup>
const version = __VERSION__;
</script>

# Getting Started

To get started, apply the plugin to your project using the
[plugin DSL](https://docs.gradle.org/current/userguide/plugins_intermediate.html#sec:plugins_block):

::: code-group
<<< @/src/docs/gradle/overview/plugin-application.gradle.kts{kotlin-vue} [build.gradle.kts]
<<< @/src/docs/gradle/overview/plugin-application.gradle{groovy-vue} [build.gradle]
:::

> [!TIP]
> The plugin is published to [Maven Central](https://central.sonatype.com). If your company uses a private or internal
> repository manager such as [Sonatype Nexus Repository](https://www.sonatype.com/products/sonatype-nexus-repository),
> ensure that it is configured to proxy Maven Central.

## Code Generation

The plugin provides two options for generating code:

1. Tasks approach based on [`JavaExec`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html) task type.
2. Options approach based on the [Worker API](https://docs.gradle.org/current/userguide/worker_api.html).

Both approaches provide a similar API. It is _recommended_ to use the options approach as it provides better parallelism
and integrates directly with Apache CXF's tooling.

> [!WARNING]
> These approaches are mutually exclusive. You can only use one of them in a single project.
> This is because both approaches register similarly named tasks and would conflict with each other.

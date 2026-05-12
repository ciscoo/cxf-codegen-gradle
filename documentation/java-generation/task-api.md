# Task API

> [!TIP]
> The [Worker API](worker-api.md) is the preferred approach for Java code generation

The Task API approach uses Gradle tasks to execute the `wsdl2java` tool. For each WSDL document to process, you must
create a separate Gradle task of type `Wsdl2Java`. 

The `Wsdl2Java` task is provided by this plugin and is a subclass of Gradle's
[`JavaExec`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html). As such, any and all configuration
options available on `JavaExec` are also available on `Wsdl2Java`.

## Configuration

For each WSDL document to process, create a task that holds the configuration. The task should be of type of `Wsdl2Java`.
Create the task using your preferred method of task creation.

The following example shows a single task named `example` that processes the `example.wsdl` document:

::: code-group
<<< @/build/processed-examples/generating-java/minimal-task-usage.gradle.kts#code{kotlin} [Kotlin]
<<< @/build/processed-examples/generating-java/minimal-task-usage.gradle#code{groovy} [Groovy]
:::

By convention, the generated Java sources will be created in the`$buildDir/$name-wsdl2java-generated-sources` directory
where `$name` is the task name.

Additionally, when defining a task:

1. For each task, the generated Java sources (the task output) is added to the `main` source sets.
   * This can be disabled when creating the task by configuring `addToMainSourceSet` to `false`.
     This is a property on the task itself.
2. All `Wsdl2Java` task types are aggregated to a single task named `wsdl2java`.

## Tool Options

The `Wsdl2Java` task type provides type-safe properties that map to the underlying `wsdl2java` tool options.
The properties are
all [lazy properties](https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties).
Their values are _realized_ when the task is executed.

The table below lists the available properties, descriptions, and type. These are a direct mapping to the `wsdl2java`
tool options listed in the tool's documentation [here](https://cxf.apache.org/docs/wsdl-to-java.html#wsdl2java-options).

These options should be configured within the `toolOptions { }` block as shown above in [Configuration](#configuration).

<!--@include: ../parts/java-options-table.md-->

## Logging

Apache CXF uses SLF4J for logging and by default, all logs are suppressed by default. This is accomplished by the
inclusion of the`org.slf4j:slf4j-nop` dependency in the `cxfCodegen` configuration.

If logs were not suppressed by default, when executing any of the created tasks, the following lines will be printed to
the console:

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

The above logging provides no useful information in the build output and instead pollutes the build output. This is the
reason why logging is suppressed by default.

### Enable Logging

To enable logging for Apache CXF:

1. Exclude the `org.slf4j:slf4j-nop` dependency
2. Include a SLF4J provider (or binding)
3. Include a logging library
   * The library should implement the SLF4J API

In the following example, [Logback](https://logback.qos.ch) is used which provides an implementation of the SLF4J API.

::: code-group
<<< @/build/processed-examples/generating-java/logging-task.gradle.kts#code{kotlin} [Kotlin]
<<< @/build/processed-examples/generating-java/logging-task.gradle#code{groovy} [Groovy]
:::

With the above configuration, you will see logs from Apache CXF:

```
11:00:38.266 [main] DEBUG org.apache.cxf.common.logging.LogUtils -- Using org.apache.cxf.common.logging.Slf4jLogger for logging.
11:00:38.319 [main] DEBUG org.apache.cxf.tools.wsdlto.core.PluginLoader -- Loading plugin jar:file:~/.gradle/caches/modules-2/files-2.1/org.apache.cxf/cxf-tools-wsdlto-databinding-jaxb/4.0.0/5876acee034617f4f6ed756105fe9a0dc50a750c/cxf-tools-wsdlto-databinding-jaxb-4.0.0.jar!/META-INF/tools-plugin.xml
11:00:38.349 [main] DEBUG org.apache.cxf.tools.wsdlto.core.PluginLoader -- Found 1 databindings in <jaxb> plugin.
11:00:38.349 [main] DEBUG org.apache.cxf.tools.wsdlto.core.PluginLoader -- Loading <jaxb> databinding from <jaxb> plugin.
11:00:38.349 [main] DEBUG org.apache.cxf.tools.wsdlto.core.PluginLoader -- Loading plugin jar:file:~/.gradle/caches/modules-2/files-2.1/org.apache.cxf/cxf-tools-wsdlto-frontend-jaxws/4.0.0/762d4f960e3e8778af050a271218785567bc29e1/cxf-tools-wsdlto-frontend-jaxws-4.0.0.jar!/META-INF/tools-plugin.xml

...and so on
```
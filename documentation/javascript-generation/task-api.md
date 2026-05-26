# Task API

> [!TIP]
> The [Worker API](worker-api.md) is the preferred approach for JavaScript code generation

The Task API approach uses Gradle tasks to execute the `wsdl2js` tool. For each WSDL document to process, you must
create a separate Gradle task of type `Wsdl2Js`. 

The `Wsdl2Js` task is provided by this plugin and is a subclass of Gradle's
[`JavaExec`](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html). As such, any and all configuration
options available on `JavaExec` are also available on `Wsdl2Js`.

## Configuration

For each WSDL document to process, create a task that holds the configuration. The task should be of type of `Wsdl2Js`.
Create the task using your preferred method of task creation.

The following example shows a single task named `example` that processes the `example.wsdl` document:

::: code-group
<<< @/build/processed-examples/generating-javascript/minimal-task-usage.gradle.kts#code{kotlin} [Kotlin]
<<< @/build/processed-examples/generating-javascript/minimal-task-usage.gradle#code{groovy} [Groovy]
:::

By convention, the generated JavaScript sources will be created in the`$buildDir/$name-wsdl2js-generated-sources` directory
where `$name` is the task name.

## Tool Options

The `Wsdl2Js` task type provides type-safe properties that map to the underlying `wsdl2js` tool options.
The properties are
all [lazy properties](https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties).
Their values are _realized_ when the task is executed.

The table below lists the available properties, descriptions, and type. These are a direct mapping to the `wsdl2js`
tool options listed in the tool's documentation [here](https://cxf.apache.org/docs/wsdl-to-javascript.html#WSDLtoJavascript-Options).

These options should be configured within the `toolOptions { }` block as shown above in [Configuration](#configuration).

<!--@include: ../parts/javascript-options-table.md-->

## Logging

Refer to the [logging documentation](/java-generation/task-api.md#logging) for generating Java code for details; the configuration is the same.

# Worker API

> [!CAUTION]
> The Worker API approach is an incubating feature and is subject to change in future releases.

The Worker API approach uses Gradle's Worker API to execute the `wsdl2js` tool in isolation. Configuration options
represent the unit of work submitted to Gradle's Worker API. This allows Gradle to parallelize the work, fully utilize
resources available on the host machine, and complete builds faster.

## Enablement

The Worker API approach is not enabled by default since it is an incubating feature.

To enable it, set the`io.mateo.cxf-codegen.workers` Gradle property to `true`.

This can be accomplished in several ways such as configuring it in the `gradle.properties` file or passing it as a
command-line argument when invoking Gradle. The properties file approach is shown below.

::: code-group

```properties [gradle.properties]
io.mateo.cxf-codegen.workers=true
```

:::

## Configuration

For each WSDL document to process, create an _option_ that holds the configuration for that unit of work. The option
should be an instance of `Wsdl2JsOption`.

To create an option, use the `options` container through the `cxfCodegen` extension.

The following example shows a single option named `example` that processes the `example.wsdl` document:

::: code-group
<<< @/build/processed-examples/generating-javascript/worker-option.gradle.kts#code{kotlin} [Kotlin]
<<< @/build/processed-examples/generating-javascript/worker-option.gradle#code{groovy} [Groovy]
:::

By convention, the generated JavaScript sources will be created in the`$buildDir/$name-wsdl2js-generated-sources` directory
where `$name` is the option name.

## Tool Options

The `Wsdl2JsOption` type provides type-safe properties that map to the underlying `wsdl2js` tool options.
The properties are
all [lazy properties](https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties).
Their values are _realized_ when the task is executed.

The table below lists the available properties, descriptions, and type. These are a direct mapping to the `wsdl2js`
tool options listed in the tool's documentation [here](https://cxf.apache.org/docs/wsdl-to-javascript.html).

<!--@include: ../parts/javascript-options-table.md-->

## Logging

The `wsdl2js` tool's logging output can be viewed when executing Gradle with the `-d`/`--debug` flag. This will show
the logging output from the tool in the console. The tool logs at the `FINE` level, so the debug flag is required to see
the output.

```
2026-04-18T18:15:04.136-0500 [DEBUG] [org.apache.cxf.common.logging.LogUtils] Using org.apache.cxf.common.logging.Slf4jLogger for logging.
2026-04-18T18:15:04.138-0500 [DEBUG] [jakarta.xml.bind] Resolved classes from context path: [class org.apache.cxf.tools.plugin.ObjectFactory]
2026-04-18T18:15:04.138-0500 [DEBUG] [jakarta.xml.bind] Checking system property jakarta.xml.bind.JAXBContextFactory
2026-04-18T18:15:04.138-0500 [DEBUG] [jakarta.xml.bind]   not found
2026-04-18T18:15:04.139-0500 [DEBUG] [jakarta.xml.bind] ServiceProvider loading Facility used; returning object [org.glassfish.jaxb.runtime.v2.JAXBContextFactory]
2026-04-18T18:15:04.139-0500 [DEBUG] [jakarta.xml.bind] Using jakarta.xml.bind-api on the class path.

...and so on
```

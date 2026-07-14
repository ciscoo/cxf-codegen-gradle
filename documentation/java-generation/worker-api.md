# Worker API

> [!CAUTION]
> The Worker API approach is an incubating feature and is subject to change in future releases.

The Worker API approach uses Gradle's Worker API to execute the `wsdl2java` tool in isolation. Configuration options
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
should be an instance of `Wsdl2JavaOption`.

To create an option, use the `options` container through the `cxfCodegen` extension.

The following example shows a single option named `example` that processes the `example.wsdl` document:

::: code-group
<<< @/build/processed-examples/generating-java/worker-option.gradle.kts#code{kotlin} [Kotlin]
<<< @/build/processed-examples/generating-java/worker-option.gradle#code{groovy} [Groovy]
:::

By convention, the generated Java sources will be created in the`$buildDir/$name-wsdl2java-generated-sources` directory
where `$name` is the option name.

### Add to `main` Source Set

The generated Java code is automatically added to the `main` source set. This means that the generated code will be
available for use in your project's application code, typically located in the `src/main/java` directory.

If you do not want the generated code to be added to the `main` source set, you can set the `addToMainSourceSet` property
to false as shown below:

::: code-group
<<< @/build/processed-examples/generating-java/disable-main-inclusion.gradle.kts#code{kotlin} [Kotlin]
<<< @/build/processed-examples/generating-java/disable-main-inclusion.gradle#code{groovy} [Groovy]
:::

## Tool Options

The `Wsdl2JavaOption` type provides type-safe properties that map to the underlying `wsdl2java` tool options.
The properties are
all [lazy properties](https://docs.gradle.org/current/userguide/lazy_configuration.html#lazy_properties).
Their values are _realized_ when the task is executed.

The table below lists the available properties, descriptions, and type. These are a direct mapping to the `wsdl2java`
tool options listed in the tool's documentation [here](https://cxf.apache.org/docs/wsdl-to-java.html#wsdl2java-options).

<!--@include: ../parts/java-options-table.md-->

## Logging

The `wsdl2java` tool's logging output can be viewed when executing Gradle with the `-d`/`--debug` flag. This will show
the logging output from the tool in the console. The tool logs at the `FINE` level, so the debug flag is required to see
the output.

```
2026-04-18T18:15:04.136-0500 [DEBUG] [org.apache.cxf.common.logging.LogUtils] Using org.apache.cxf.common.logging.Slf4jLogger for logging.
2026-04-18T18:15:04.138-0500 [DEBUG] [jakarta.xml.bind] Resolved classes from context path: [class org.apache.cxf.tools.plugin.ObjectFactory]
2026-04-18T18:15:04.138-0500 [DEBUG] [jakarta.xml.bind] Checking system property jakarta.xml.bind.JAXBContextFactory
2026-04-18T18:15:04.138-0500 [DEBUG] [jakarta.xml.bind]   not found
2026-04-18T18:15:04.139-0500 [DEBUG] [jakarta.xml.bind] ServiceProvider loading Facility used; returning object [org.glassfish.jaxb.runtime.v2.JAXBContextFactory]
2026-04-18T18:15:04.139-0500 [DEBUG] [jakarta.xml.bind] Using jakarta.xml.bind-api on the class path.
2026-04-18T18:15:04.142-0500 [DEBUG] [org.glassfish.jaxb.runtime.v2.ContextFactory] Property org.glassfish.jaxb.XmlAccessorFactoryis not active.  Using JAXB's implementation
2026-04-18T18:15:04.186-0500 [DEBUG] [org.apache.cxf.tools.wsdlto.core.PluginLoader] Loading plugin jar:file:/Users/example/.gradle/caches/modules-2/files-2.1/org.apache.cxf/cxf-tools-wsdlto-databinding-jaxb/4.1.3/.../cxf-tools-wsdlto-databinding-jaxb-4.1.3.jar!/META-INF/tools-plugin.xml
2026-04-18T18:15:04.234-0500 [DEBUG] [org.apache.cxf.tools.wsdlto.core.PluginLoader] Found 1 databindings in <jaxb> plugin.
2026-04-18T18:15:04.234-0500 [DEBUG] [org.apache.cxf.tools.wsdlto.core.PluginLoader] Loading <jaxb> databinding from <jaxb> plugin.
2026-04-18T18:15:04.234-0500 [DEBUG] [org.apache.cxf.tools.wsdlto.core.PluginLoader] Loading plugin jar:file:/Users/example/.gradle/caches/modules-2/files-2.1/org.apache.cxf/cxf-tools-wsdlto-frontend-jaxws/4.1.3/.../cxf-tools-wsdlto-frontend-jaxws-4.1.3.jar!/META-INF/tools-plugin.xml

...and so on
```

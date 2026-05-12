# Loading A WSDL From A Maven Repository

Loading a WSDL from a remote artifact can be accomplished using native Gradle APIs as shown below.
For example, to use the WSDL published in the `cxf-testutils` JAR artifact:

## Workers

::: code-group
<<< @/build/processed-examples/examples/loading-wsdl-workers.gradle.kts#code{3,6,9,18,35 kotlin} [Kotlin]
<<< @/build/processed-examples/examples/loading-wsdl-workers.gradle#code{4,8,11,20,39 groovy} [Groovy]
:::

1. Define a configuration to hold the dependency.
2. Add the dependency containing a WSDL to the configuration.
3. Define a task to copy the dependency that contains the WSDL.
4. Extract the WSDL from the artifact.
5. Use the task output as input to option configuration.

## Task

::: code-group
<<< @/build/processed-examples/examples/loading-wsdl.gradle.kts#code{3,6,9,18,35 kotlin} [Kotlin]
<<< @/build/processed-examples/examples/loading-wsdl.gradle#code{4,8,11,20,39 groovy} [Groovy]
:::

1. Define a configuration to hold the dependency.
2. Add the dependency containing a WSDL to the configuration.
3. Define a task to copy the dependency that contains the WSDL.
4. Extract the WSDL from the artifact.
5. Use the task output as input to the `Wsdl2Java` task.

# Using XJC Extensions

## Workers

::: code-group
<<< @/build/processed-examples/examples/using-xjc-extensions-workers.gradle.kts#code{6-7,13 kotlin} [Kotlin]
<<< @/build/processed-examples/examples/using-xjc-extensions-workers.gradle#code{6-7,13 groovy} [Groovy]
:::

1. Add the required CXF JXC dependency to your application dependencies.
2. Add the extension dependency to the tool classpath.
3. Specify extension ID that corresponds to the added dependency from (2); the `-xjc` prefix is not required when using
   the `xjcArgs` property.

## Task

::: code-group
<<< @/build/processed-examples/examples/using-xjc-extensions.gradle.kts#code{6-7,13 kotlin} [Kotlin]
<<< @/build/processed-examples/examples/using-xjc-extensions.gradle#code{6-7,13 groovy} [Groovy]
:::

1. Add the required CXF JXC dependency to your application dependencies.
2. Add the extension dependency to the tool classpath.
3. Specify extension ID that corresponds to the added dependency from (2); the `-xjc` prefix is not required when using
   the `xjcArgs` property.

---
next: Java Generation - Overview
---

<script setup>
const cxfVersion = __CXF_VERSION__;
const cxfPomLink = `https://github.com/apache/cxf/blob/cxf-${cxfVersion}/maven-plugins/codegen-plugin/pom.xml#L71...L106`;

const decrementPatch = version => {
  const [major, minor, patchStr] = version.split('.');
  const patch = Number(patchStr);
  return `${major}.${minor}.${patch - 1}`;
};

const previousCxfVersion = decrementPatch(cxfVersion);
</script>

# Dependency Management

The plugin adds a single
[dependency configuration](https://docs.gradle.org/current/userguide/dependency_configurations.html#sub:what-are-dependency-configurations)
to the project named `cxfCodegen`.

The configuration is used to manage the dependencies required by Apache CXF for code generation.

## Default Dependencies

The following dependencies are included by default in the `cxfCodegen` configuration:

| Dependency                                            | Verson           | Exclusions                              |
| ----------------------------------------------------- | ---------------- | --------------------------------------- |
| `org.apache.cxf:cxf-core`                             | {{ cxfVersion }} | None                                    |
| `org.apache.cxf:cxf-tools-common`                     | {{ cxfVersion }} | None                                    |
| `org.apache.cxf:cxf-tools-wsdlto-core`                | {{ cxfVersion }} | None                                    |
| `org.apache.cxf:cxf-tools-wsdlto-databinding-jaxb`    | {{ cxfVersion }} | None                                    |
| `org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws`      | {{ cxfVersion }} | None                                    |
| `org.apache.cxf:cxf-tools-wsdlto-frontend-javascript` | {{ cxfVersion }} | `org.apache.cxf:cxf-rt-frontend-simple` |

These are the same dependencies defined in the <a :href="cxfPomLink">Maven plugin's POM</a>.

### CXF Version

The version of CXF can be controlled by configuring the `cxfVersion` property on the extension. The convention of the
version is **{{ cxfVersion }}**.

If this convention does not work for your project, override the `cxfVersion` property on the extension to specify a
different version.

The following example shows how to override the CXF version to {{ previousCxfVersion }}.

::: code-group
<<< @/src/docs/gradle/plugin-configuration/cxf-version.gradle.kts#code{2 kotlin-vue} [Kotlin]
<<< @/src/docs/gradle/plugin-configuration/cxf-version.gradle#code{2 groovy-vue} [Groovy]
:::

> [!WARNING]
> You will need to ensure the version of CXF you specify is compatible with the tool options used **and** your
> application's version of [Jakarta EE](https://jakarta.ee/specifications/)
>
> Failure to do so can result in an error during code generation, compilation errors, runtime errors, or a combination
> of these.

The `cxfVersion` property is a Gradle type `Property<String>` which enables
[lazy configuration](https://docs.gradle.org/current/userguide/lazy_configuration.html).

Refer to Gradle's
[Understanding Properties](https://docs.gradle.org/current/userguide/properties_providers.html#understanding_properties)
documentation to better understand this concept.

> [!TIP]
> You can use any Gradle dependency management configuration to configure dependency versions. For example,
> a [resolution rule](https://docs.gradle.org/current/userguide/resolution_rules.html#sec:dependency_resolve_rules) can
> be configured to force a specific version of a dependency.
>
> The property approach shown above is preferred for its simplicity.

## Adding Dependencies

The `cxfCodegen` configuration can be used to add additional dependencies to the code generation classpath.

The following example shows adding the `cxf-rt-databinding-jibx` dependency to the `cxfCodegen` configuration.
This dependency will be included in the code generation classpath.

::: code-group
<<< @/src/docs/gradle/plugin-configuration/dependency-management.gradle.kts#code{kotlin-vue} [Kotlin]
<<< @/src/docs/gradle/plugin-configuration/dependency-management.gradle#code{groovy-vue} [Groovy]
:::

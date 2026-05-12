<script setup>
const gradleVersion = __GRADLE_BUILD_VERSION__;
</script>

# Examples

This section provides Gradle examples translated from the
[Maven plugin examples](https://cxf.apache.org/docs/maven-cxf-codegen-plugin-wsdl-to-java.html).

> [!CAUTION]
> You will need to ensure you have the appropriate dependencies when generating code. Additionally, there may be more
> dependencies the CXF Codegen tool requires. Review the [adding dependencies](/configuration/dependency-management##adding-dependencies)
> documentation on how to add additional dependencies to the CXF Codegen tool classpath.
>
> In addition to the tool dependencies, your application may require additional dependencies as well.

> [!CAUTION]
> All examples are written for _and_ tested for Gradle {{ gradleVersion }}. Depending on your Gradle version, you may
> need to adapt the example to a syntax that is compatible with your Gradle version.

> [!INFO]
> The following examples have been omitted:
>
> 1. [Using wsdlRoot with includes/excludes patterns](https://cxf.apache.org/docs/maven-cxf-codegen-plugin-wsdl-to-java.html#Mavencxfcodegenplugin(WSDLtoJava)-Example5:UsingwsdlRootwithincludes/excludespatterns)
>    * It is a design decision of the plugin to omit the functionality to define a `wsdlRoot` or "base path" to locate WSDL(s).
>    * It is responsibility of the user to provide the location a WSDL. This keeps the plugin codebase simpler.
>
> 2. [Using defaultOption to avoid repetition](https://cxf.apache.org/docs/maven-cxf-codegen-plugin-wsdl-to-java.html#Mavencxfcodegenplugin(WSDLtoJava)-Example4:UsingdefaultOptiontoavoidrepetition)
>    * The `defaultOptions` configuration does not exist in this Gradle plugin. Instead, use native Gradle APIs to
>      accomplish the same behavior.

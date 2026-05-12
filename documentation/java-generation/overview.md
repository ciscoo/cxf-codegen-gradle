# Overview

Java sources can be generated from a single WSDL or several WSDL documents. This is accomplished through the
[`wsdl2java`](https://cxf.apache.org/docs/wsdl-to-java.html) tool provided by the Apache CXF project.

The generated Java sources can then be compiled and used in your Java applications to interact with Web services or
published as a Java library for reuse in other Java applications or libraries.

The plugin provides type-safe configuration that maps to the underlying `wsdl2java` tool options.

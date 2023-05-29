import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

tasks.register("example", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toString())
    }
}

// tag::code[]
tasks.withType(Wsdl2Java::class).configureEach {
    jvmArgs = listOf("-Dorg.apache.cxf.Logger=null")
}
// end::code[]

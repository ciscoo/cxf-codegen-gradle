import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toString())
        markGenerated.set(true)
    }
}

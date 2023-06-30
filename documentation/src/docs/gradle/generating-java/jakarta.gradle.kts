import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

// tag::code[]
repositories {
    mavenCentral()
}

cxfCodegen {
    cxfVersion.set("4.0.0") // <1>
}

dependencies {
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:4.0.0") // <2>
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:2.1.1")
}
// end::code[]

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("calculator.wsdl").toPath().toAbsolutePath().toString())
    }
}
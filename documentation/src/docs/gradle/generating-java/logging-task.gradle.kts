import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    id("java")
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// tag::code[]
dependencies {
    cxfCodegen("ch.qos.logback:logback-classic:1.2.10")
}
// end::code[]

tasks.register("example", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("wsdls/calculator.wsdl"))
    }
}

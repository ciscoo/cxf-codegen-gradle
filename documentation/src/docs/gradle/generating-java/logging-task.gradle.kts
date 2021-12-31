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

dependencies {
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5")
}

tasks.register("example", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("wsdls/calculator.wsdl"))
    }
}

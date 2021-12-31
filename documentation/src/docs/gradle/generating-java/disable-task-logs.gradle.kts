import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

dependencies {
    cxfCodegen("ch.qos.logback:logback-classic:1.2.3")
    cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5")
}

tasks.register("example", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("wsdls/calculator.wsdl"))
    }
}

// tag::code[]
tasks.withType(Wsdl2Java::class).configureEach {
    jvmArgs = listOf("-Dorg.apache.cxf.Logger=null")
}
// end::code[]

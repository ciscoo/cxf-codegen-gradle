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
    cxfCodegen("ch.qos.logback:logback-classic:1.4.7")
}
// end::code[]

tasks.register("example", Wsdl2Java::class) {
    jvmArgs = listOf("-Dorg.apache.cxf.Logger=org.apache.cxf.common.logging.Slf4jLogger")
    toolOptions {
        wsdl.set(file("wsdls/calculator.wsdl"))
    }
}

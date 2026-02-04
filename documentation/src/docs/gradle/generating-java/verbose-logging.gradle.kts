import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

configurations.cxfCodegen {
    exclude(group = "org.slf4j", module = "slf4j-nop")
}

dependencies {
    cxfCodegen("ch.qos.logback:logback-classic:1.5.27")
}

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl = file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toString()
    }
}

// tag::code[]
tasks.withType(Wsdl2Java::class).configureEach {
    jvmArgs = listOf("-Dorg.apache.cxf.Logger=org.apache.cxf.common.logging.Slf4jLogger")
}
// end::code[]

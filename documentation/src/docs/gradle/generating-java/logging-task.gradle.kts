import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    id("java")
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

// tag::code[]
configurations.cxfCodegen {
    exclude(group = "org.slf4j", module = "slf4j-nop")
}

dependencies {
    cxfCodegen("ch.qos.logback:logback-classic:1.5.32")
}
// end::code[]

tasks.register("example", Wsdl2Java::class) {
    jvmArgs = listOf("-Dorg.apache.cxf.Logger=org.apache.cxf.common.logging.Slf4jLogger")
    toolOptions {
        wsdl = file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toString()
    }
}

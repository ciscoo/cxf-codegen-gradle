import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
    java
    id("io.mateo.cxf-codegen")
}

repositories {
    mavenCentral()
}

dependencies {
    cxfCodegen("ch.qos.logback:logback-classic:1.4.7")
}

tasks.register("calculator", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("wsdls/calculator.wsdl").toPath().toAbsolutePath().toUri().toString())
    }
}

// tag::code[]
tasks.withType(Wsdl2Java::class).configureEach {
    jvmArgs = listOf("-Dorg.apache.cxf.Logger=org.apache.cxf.common.logging.Slf4jLogger")
}
// end::code[]

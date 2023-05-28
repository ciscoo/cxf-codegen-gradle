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

tasks.register("example", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("example.wsdl").toPath().toAbsolutePath().toUri().toString())
    }
}

//tag::code[]
tasks.withType(Wsdl2Java::class).configureEach {
    jvmArgs = listOf("-Dlogback.configurationFile=logback.xml")
}
//end::code[]

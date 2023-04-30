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
}

tasks.register("example", Wsdl2Java::class) {
    toolOptions {
        wsdl.set(file("example.wsdl"))
    }
}

//tag::code[]
tasks.withType(Wsdl2Java::class).configureEach {
    jvmArgs = listOf("-Dlogback.configurationFile=logback.xml")
}
//end::code[]
